package ru.yandex.javacourse.zolotyh.schedule.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        System.out.println("Началась обработка /epic запроса от клиента.");
    }
}
