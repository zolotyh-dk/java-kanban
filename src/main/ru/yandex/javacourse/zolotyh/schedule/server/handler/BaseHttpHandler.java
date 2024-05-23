package ru.yandex.javacourse.zolotyh.schedule.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static ru.yandex.javacourse.zolotyh.schedule.server.HttpTaskServer.*;

public class BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    protected BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

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
