package ru.yandex.javacourse.zolotyh.schedule.server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.exception.InvalidTaskException;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.TaskManager;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        System.out.println("Началась обработка /tasks запроса от клиента.");
        try (httpExchange) {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case GET:
                    if (Pattern.matches("^/tasks$", path)) {
                        handleGetTasks(httpExchange); //GET /tasks
                    } else if (Pattern.matches("^/tasks/\\d+$", path)) {
                        handleGetTaskById(httpExchange, path); //GET /tasks/{id}
                    } else {
                        sendBadRequest(httpExchange); //400 - если для не существует запрошенной комбинации метода и пути
                    }
                    break;

                case POST:
                    if (Pattern.matches("^/tasks$", path)) {
                        handleAddOrUpdateTask(httpExchange); //POST /tasks
                    } else {
                        sendBadRequest(httpExchange); //400 - если для не существует запрошенной комбинации метода и пути
                    }
                    break;

                case DELETE:
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        handleDeleteTaskById(httpExchange, path); //DELETE /tasks{id}
                    } else {
                        sendBadRequest(httpExchange); //400 - если для не существует запрошенной комбинации метода и пути
                    }
                    break;
                default:
                    sendMethodNotAllowed(httpExchange); //405 - если метод не соответствует ни одному из эндпоинтов
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGetTasks(HttpExchange httpExchange) {
        System.out.println("Клиент запросил список всех задач");
        String response = gson.toJson(taskManager.getAllTasks());
        sendText(httpExchange, response); //200
    }

    private void handleGetTaskById(HttpExchange httpExchange, String path) {
        String pathId = path.replaceFirst("/tasks/", "");
        int id = Integer.parseInt(pathId);
        System.out.println("Запрошена задача с id=" + id);
        try {
            String response = gson.toJson(taskManager.getTaskById(id));
            sendText(httpExchange, response); //200
        } catch (NoSuchElementException e) {
            sendNotFound(httpExchange); //404 - если задачи нет
        }
    }

    private void handleAddOrUpdateTask(HttpExchange httpExchange) throws IOException {
        String json = readText(httpExchange);
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        if (!jsonObject.has("id") ||
                !jsonObject.has("name") ||
                !jsonObject.has("description") ||
                !jsonObject.has("status") ||
                !jsonObject.has("duration") ||
                !jsonObject.has("startTime")
        ) {
            sendBadRequest(httpExchange); //400 - если в JSON нет какого-то из полей
            return;
        }
        Task task = parseTaskFromJson(jsonObject);
        System.out.println("Десериализовали задачу: " + task);
        try {
            if (task.getId() == null) {
                taskManager.addNewTask(task);
                System.out.println("Добавлена новая задача c id=" + task.getId());
                sendCreated(httpExchange); //201

            } else {
                taskManager.updateTask(task);
                System.out.println("Обновили задачу c id=" + task.getId());
                sendCreated(httpExchange); //201
            }
        } catch (InvalidTaskException e) {
            sendHasInteractions(httpExchange); // 406 - если задача пересекается с существующими
        }
    }

    private void handleDeleteTaskById(HttpExchange httpExchange, String path) {
        String pathId = path.replaceFirst("/tasks/", "");
        int id = Integer.parseInt(pathId);
        System.out.println("Удаляем задачу с id=" + id);
        taskManager.deleteTask(id);
        sendOk(httpExchange); //200
    }

    private Task parseTaskFromJson(JsonObject jsonObject) {
        Integer id = null;
        if (!jsonObject.get("id").isJsonNull()) {
            id = jsonObject.get("id").getAsInt();
        }
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        Status status = Status.valueOf(jsonObject.get("status").getAsString());
        Duration duration = null;
        LocalDateTime startTime = null;
        if (!jsonObject.get("duration").isJsonNull() && !jsonObject.get("startTime").isJsonNull()) {
            duration = Duration.parse(jsonObject.get("duration").getAsString());
            startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());
        }
        return new Task(id, name, description, status, duration, startTime);
    }
}
