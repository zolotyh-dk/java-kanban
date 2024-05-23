package ru.yandex.javacourse.zolotyh.schedule.server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.zolotyh.schedule.exception.InvalidTaskException;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.TaskManager;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import static ru.yandex.javacourse.zolotyh.schedule.server.HttpTaskServer.*;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        System.out.println("Началась обработка /epics запроса от клиента.");
        try (httpExchange) {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case GET:
                    if (Pattern.matches("^/epics$", path)) {
                        handleGetEpics(httpExchange); //GET /epics
                    } else if (Pattern.matches("^/epics/\\d+$", path)) {
                        handleGetEpicById(httpExchange, path); //GET /epics/{id}
                    } else if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
                        handleGetEpicSubtasks(httpExchange, path);
                    } else {
                        sendBadRequest(httpExchange); //400 - если для не существует запрошенной комбинации метода и пути
                    }
                    break;

                case POST:
                    if (Pattern.matches("^/epics$", path)) {
                        handleAddOrUpdateEpic(httpExchange); //POST /epics
                    } else {
                        sendBadRequest(httpExchange); //400 - если для не существует запрошенной комбинации метода и пути
                    }
                    break;

                case DELETE:
                    if (Pattern.matches("^/epics/\\d+$", path)) {
                        handleDeleteEpicById(httpExchange, path); //DELETE /epics{id}
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

    private void handleGetEpics(HttpExchange httpExchange) {
        System.out.println("Клиент запросил список всех эпиков");
        String response = gson.toJson(taskManager.getAllEpics());
        sendText(httpExchange, response); //200
    }

    private void handleGetEpicById(HttpExchange httpExchange, String path) {
        String pathId = path.replaceFirst("/epics/", "");
        int id = Integer.parseInt(pathId);
        System.out.println("Запрошен эпик с id=" + id);
        try {
            String response = gson.toJson(taskManager.getEpicById(id));
            sendText(httpExchange, response); //200
        } catch (NoSuchElementException e) {
            sendNotFound(httpExchange); //404 - если задачи нет
        }
    }

    private void handleGetEpicSubtasks(HttpExchange httpExchange, String path) {
        String pathId = path.replaceFirst("/epics/", "").replace("/subtasks", "");
        int id = Integer.parseInt(pathId);
        System.out.println("Запрошены подзадачи эпика с id=" + id);
        try {
            String response = gson.toJson(taskManager.getSubtasksByEpic(taskManager.getEpicById(id)));
            sendText(httpExchange, response); //200
        } catch (NoSuchElementException e) {
            sendNotFound(httpExchange); //404 - если эпика нет
        }
    }

    private void handleAddOrUpdateEpic(HttpExchange httpExchange) throws IOException {
        String json = readText(httpExchange);
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        if (!jsonObject.has("id") ||
                !jsonObject.has("name") ||
                !jsonObject.has("description")
        ) {
            sendBadRequest(httpExchange); //400 - если в JSON нет какого-то из полей
            return;
        }
        Epic epic = parseEpicFromJson(jsonObject);
        System.out.println("Десериализовали эпик: " + epic);
        try {
            if (epic.getId() == null) {
                taskManager.addNewEpic(epic);
                System.out.println("Добавлена новый эпик c id=" + epic.getId());
                sendCreated(httpExchange); //201

            } else {
                taskManager.updateEpic(epic);
                System.out.println("Обновили эпик c id=" + epic.getId());
                System.out.println("Теперь эпик такой: " + taskManager.getEpicById(epic.getId()));
                sendCreated(httpExchange); //201
            }
        } catch (InvalidTaskException e) {
            sendHasInteractions(httpExchange); // 406 - если эпик пересекается с существующими
        }
    }

    private void handleDeleteEpicById(HttpExchange httpExchange, String path) {
        String pathId = path.replaceFirst("/epics/", "");
        int id = Integer.parseInt(pathId);
        System.out.println("Удаляем эпик с id=" + id);
        taskManager.deleteEpic(id);
        sendOk(httpExchange); //200
    }

    private Epic parseEpicFromJson(JsonObject jsonObject) {
        Integer id = null;
        if (!jsonObject.get("id").isJsonNull()) {
            id = jsonObject.get("id").getAsInt();
        }
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        return new Epic(id, name, description);
    }
}
