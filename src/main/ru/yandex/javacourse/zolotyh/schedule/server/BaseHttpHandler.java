package ru.yandex.javacourse.zolotyh.schedule.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    protected BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";

    public static final int OK = 200; //если сервер корректно выполнил запрос и вернул данные
    public static final int CREATED = 201; //если запрос выполнен успешно, но возвращать данные нет необходимости
    public static final int BAD_REQUEST = 400; //если для не существует запрошенной комбинации метода и пути
    public static final int NOT_FOUND = 404; //если пользователь обратился к несуществующему ресурсу
    public static final int METHOD_NOT_ALLOWED = 405; //если метод не соответствует ни одному из эндпоинтов
    public static final int NOT_ACCEPTABLE = 406; //если добавляемая задача пересекается с существующими
    public static final int INTERNAL_SERVER_ERROR = 500; //если произошла ошибка при обработке запроса

    protected void sendText(HttpExchange httpExchange, String text) {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        sendResponse(httpExchange, OK, response);
    }

    protected void sendOk(HttpExchange httpExchange) {
        sendResponse(httpExchange, OK, null);
    }

    protected void sendCreated(HttpExchange httpExchange) {
        sendResponse(httpExchange, CREATED, null);
    }

    protected void sendNotFound(HttpExchange httpExchange) {
        sendResponse(httpExchange, NOT_FOUND, null);
    }

    protected void sendHasInteractions(HttpExchange httpExchange) {
        sendResponse(httpExchange, NOT_ACCEPTABLE, null);
    }

    protected void sendMethodNotAllowed(HttpExchange httpExchange) {
        sendResponse(httpExchange, METHOD_NOT_ALLOWED, null);
    }

    protected void sendBadRequest(HttpExchange httpExchange) {
        sendResponse(httpExchange, BAD_REQUEST, null);
    }

    protected void sendInternalServerError(HttpExchange httpExchange) {
        sendResponse(httpExchange, INTERNAL_SERVER_ERROR, null);
    }

    private void sendResponse(HttpExchange httpExchange, int statusCode, byte[] response) {
        try (OutputStream responseBody = httpExchange.getResponseBody();
             httpExchange) {
            if (response != null) {
                httpExchange.sendResponseHeaders(statusCode, response.length);
                responseBody.write(response);
            } else {
                httpExchange.sendResponseHeaders(statusCode, 0);
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка при попытке отправить ответ.");
        }
    }

    protected String readText(HttpExchange httpExchange) throws IOException {
        try (InputStream requestBody = httpExchange.getRequestBody()) {
            String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
            return body;
        } catch (IOException e) {
            sendInternalServerError(httpExchange);
            throw e;
        }
    }
}
