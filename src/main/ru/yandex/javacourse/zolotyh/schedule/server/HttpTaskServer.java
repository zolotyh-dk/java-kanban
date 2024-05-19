package ru.yandex.javacourse.zolotyh.schedule.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer httpServer;

    public HttpTaskServer() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        } catch (IOException e) {
            throw new RuntimeException("Невозможно создать HTTP сервер", e);
        }
    }

    public void start() {
        httpServer.start();
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }
}
