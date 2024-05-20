package ru.yandex.javacourse.zolotyh.schedule.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";

    public static final int OK = 200; //если сервер корректно выполнил запрос и вернул данные
    public static final int CREATED = 201; //если запрос выполнен успешно, но возвращать данные нет необходимости
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404; //если пользователь обратился к несуществующему ресурсу
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int NOT_ACCEPTABLE = 406; //если добавляемая задача пересекается с существующими
    public static final int INTERNAL_SERVER_ERROR = 500; //если произошла ошибка при обработке запроса

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        sendResponse(httpExchange, OK, response);
    }

    protected void sendOk(HttpExchange httpExchange) throws IOException {
        sendResponse(httpExchange, OK, null);
    }

    protected void sendCreated(HttpExchange httpExchange) throws IOException {
        sendResponse(httpExchange, CREATED, null);
    }

    protected void sendNotFound(HttpExchange httpExchange) throws IOException {
        sendResponse(httpExchange, NOT_FOUND, null);
    }

    protected void sendHasInteractions(HttpExchange httpExchange) throws IOException {
        sendResponse(httpExchange, NOT_ACCEPTABLE, null);
    }

    protected void sendMethodNotAllowed(HttpExchange httpExchange) throws IOException {
        sendResponse(httpExchange, METHOD_NOT_ALLOWED, null);
    }

    protected void sendBadRequest(HttpExchange httpExchange) throws IOException {
        sendResponse(httpExchange, BAD_REQUEST, null);
    }

    protected void sendInternalServerError(HttpExchange httpExchange) throws IOException {
        sendResponse(httpExchange, INTERNAL_SERVER_ERROR, null);
    }

    private void sendResponse(HttpExchange httpExchange, int statusCode, byte[] response) throws IOException {
        try {
            if (response != null) {
                httpExchange.sendResponseHeaders(statusCode, response.length);
                httpExchange.getResponseBody().write(response);
            } else {
                httpExchange.sendResponseHeaders(statusCode, 0);
            }
        } finally {
            httpExchange.getResponseBody().close();
        }
    }

    protected String readText(HttpExchange httpExchange) throws IOException {
        try {
            String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            httpExchange.getRequestBody().close();
            return body;
        } catch (IOException e) {
            sendInternalServerError(httpExchange);
            throw e;
        }
    }
}
