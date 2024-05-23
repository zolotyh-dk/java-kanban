package ru.yandex.javacourse.zolotyh.schedule.server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.exception.InvalidTaskException;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.TaskManager;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import static ru.yandex.javacourse.zolotyh.schedule.server.HttpTaskServer.*;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        System.out.println("Началась обработка /subtask запроса от клиента.");
        try (httpExchange) {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case GET:
                    if (Pattern.matches("^/subtasks$", path)) {
                        handleGetSubtasks(httpExchange); //GET /subtasks
                    } else if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        handleGetSubtaskById(httpExchange, path); //GET /subtasks/{id}
                    } else {
                        sendBadRequest(httpExchange); //400 - если для не существует запрошенной комбинации метода и пути
                    }
                    break;

                case POST:
                    if (Pattern.matches("^/subtasks$", path)) {
                        handleAddOrUpdateSubtask(httpExchange); //POST /subtasks
                    } else {
                        sendBadRequest(httpExchange); //400 - если для не существует запрошенной комбинации метода и пути
                    }
                    break;

                case DELETE:
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        handleDeleteSubtaskById(httpExchange, path); //DELETE /subtasks{id}
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

    private void handleGetSubtasks(HttpExchange httpExchange) {
        System.out.println("Клиент запросил список всех подзадач");
        String response = gson.toJson(taskManager.getAllSubtasks());
        sendText(httpExchange, response); //200
    }

    private void handleGetSubtaskById(HttpExchange httpExchange, String path) {
        String pathId = path.replaceFirst("/subtasks/", "");
        int id = Integer.parseInt(pathId);
        System.out.println("Запрошена подзадача с id=" + id);
        try {
            String response = gson.toJson(taskManager.getSubtaskById(id));
            sendText(httpExchange, response); //200
        } catch (NoSuchElementException e) {
            sendNotFound(httpExchange); //404 - если подзадачи нет
        }
    }

    private void handleAddOrUpdateSubtask(HttpExchange httpExchange) throws IOException {
        String json = readText(httpExchange);
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        if (!jsonObject.has("id") ||
                !jsonObject.has("name") ||
                !jsonObject.has("description") ||
                !jsonObject.has("status") ||
                !jsonObject.has("duration") ||
                !jsonObject.has("startTime") ||
                !jsonObject.has("epicId")
        ) {
            sendBadRequest(httpExchange); //400 - если в JSON нет какого-то из полей
            return;
        }
        Subtask subtask = parseSubtaskFromJson(jsonObject);
        System.out.println("Десериализовали подзадачу: " + subtask);
        try {
            if (subtask.getId() == null) {
                taskManager.addNewSubtask(subtask);
                System.out.println("Добавлена новая подзадача c id=" + subtask.getId());
                sendCreated(httpExchange); //201

            } else {
                taskManager.updateSubtask(subtask);
                System.out.println("Обновили подзадачу c id=" + subtask.getId());
                sendCreated(httpExchange); //201
            }
        } catch (InvalidTaskException e) {
            sendHasInteractions(httpExchange); // 406 - если подзадача пересекается с существующими
        }
    }

    private void handleDeleteSubtaskById(HttpExchange httpExchange, String path) {
        String pathId = path.replaceFirst("/subtasks/", "");
        int id = Integer.parseInt(pathId);
        System.out.println("Удаляем подзадачу с id=" + id);
        taskManager.deleteSubtask(id);
        sendOk(httpExchange); //200
    }

    private Subtask parseSubtaskFromJson(JsonObject jsonObject) {
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
        int epicId = jsonObject.get("epicId").getAsInt();
        return new Subtask(id, name, description, status, duration, startTime, epicId);
    }
}
