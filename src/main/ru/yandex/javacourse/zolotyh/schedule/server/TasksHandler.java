package ru.yandex.javacourse.zolotyh.schedule.server;

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
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        System.out.println("Началась обработка /task запроса от клиента.");
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
                        sendMethodNotAllowed(httpExchange);
                    }
                    break;

                case POST:
                    if (Pattern.matches("^/tasks$", path)) {
                        handleAddNewTask(httpExchange); //POST /tasks
                    } else if (Pattern.matches("^/tasks/\\d+$", path)) {
                        handleUpdateTask(httpExchange, path); //POST /task/{id}
                    } else {
                        sendMethodNotAllowed(httpExchange);
                    }
                    break;

                case DELETE:
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        handleDeleteTaskById(httpExchange, path); //DELETE /tasks{id}
                    } else {
                        httpExchange.sendResponseHeaders(METHOD_NOT_ALLOWED, 0);
                    }
                    break;
                default:
                    sendMethodNotAllowed(httpExchange); //405 - если метод не соответствует ни одному из эндпоинтов
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGetTasks(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(taskManager.getAllTasks());
        sendText(httpExchange, response); //200
    }

    private void handleGetTaskById(HttpExchange httpExchange, String path) throws IOException {
        String pathId = path.replaceFirst("/tasks", "");
        int id = Integer.parseInt(pathId);
        try {
            String response = gson.toJson(taskManager.getTaskById(id));
            sendText(httpExchange, response); //200
        } catch (NoSuchElementException e) {
            sendNotFound(httpExchange); //404 - если задачи нет
        }
    }

    private void handleAddNewTask(HttpExchange httpExchange) throws IOException {
        String json = readText(httpExchange);
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        if (!jsonObject.has("name") || !jsonObject.has("description") || !jsonObject.has("status")) {
            sendBadRequest(httpExchange); //400 - если в JSON нет обязательных полей
            return;
        }
        Task newTask = parseTaskFromJson(jsonObject);
        try {
            int id = taskManager.addNewTask(newTask);
            System.out.println("Добавлена новая задача: " + taskManager.getTaskById(id));
            sendCreated(httpExchange); //201
        } catch (InvalidTaskException e) {
            sendHasInteractions(httpExchange); // 406 - если задача пересекается с существующими
        }
    }

    private void handleUpdateTask(HttpExchange httpExchange, String path) throws IOException {
        String pathId = path.replaceFirst("/tasks", "");
        int requestedId = Integer.parseInt(pathId);
        String json = readText(httpExchange);
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        if (!jsonObject.has("id") ||
                requestedId != jsonObject.get("id").getAsInt() ||
                !jsonObject.has("name") ||
                !jsonObject.has("description") ||
                !jsonObject.has("status")) {
            sendBadRequest(httpExchange); //400 - если в JSON нет обязательных полей
            return;
        }

        Task updatedTask = parseTaskFromJson(jsonObject);
        try {
            taskManager.updateTask(updatedTask);
            sendCreated(httpExchange); //201
        } catch (InvalidTaskException e) {
            sendHasInteractions(httpExchange); // 406 - если задача пересекается с существующими
        }

    }

    private void handleDeleteTaskById(HttpExchange httpExchange, String path) throws IOException {
        String pathId = path.replaceFirst("/tasks", "");
        int id = Integer.parseInt(pathId);
        taskManager.deleteTask(id);
        sendOk(httpExchange); //200
    }

    private Task parseTaskFromJson(JsonObject jsonObject) {
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        Status status = Status.valueOf(jsonObject.get("status").getAsString());
        if (jsonObject.has("duration") && jsonObject.has("startTime")) {
            Duration duration = Duration.parse(jsonObject.get("duration").getAsString());
            LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());
            return new Task(null, name, description, status, duration, startTime);
        } else {
            return new Task(null, name, description, status);
        }
    }
}
