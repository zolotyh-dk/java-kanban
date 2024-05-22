package ru.yandex.javacourse.zolotyh.schedule.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }
    @Override
    public void handle(HttpExchange exchange) {
        System.out.println("Началась обработка /prioritized запроса от клиента.");
    }
}
