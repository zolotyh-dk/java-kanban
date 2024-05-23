package ru.yandex.javacourse.zolotyh.schedule.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.TaskManager;

import java.util.regex.Pattern;

import static ru.yandex.javacourse.zolotyh.schedule.server.HttpTaskServer.GET;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        System.out.println("Началась обработка /prioritized запроса от клиента.");
        try (httpExchange) {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            if (requestMethod.equals(GET) && Pattern.matches("^/prioritized$", path)) {
                handleGetPrioritized(httpExchange);
            } else {
                sendBadRequest(httpExchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGetPrioritized(HttpExchange httpExchange) {
        System.out.println("Клиент запросил список задач в порядке приоритета");
        String response = gson.toJson(taskManager.getPrioritizedTasks());
        sendText(httpExchange, response);
    }
}