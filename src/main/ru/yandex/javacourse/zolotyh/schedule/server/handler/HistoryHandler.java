package ru.yandex.javacourse.zolotyh.schedule.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.TaskManager;

import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        System.out.println("Началась обработка /history запроса от клиента.");
        try (httpExchange) {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            if (requestMethod.equals(GET) && Pattern.matches("^/history$", path)) {
                handleGetHistory(httpExchange);
            } else {
                sendBadRequest(httpExchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGetHistory(HttpExchange httpExchange) {
        System.out.println("Клиент запросил историю просмотра");
        String response = gson.toJson(taskManager.getHistory());
        sendText(httpExchange, response);
    }
}
