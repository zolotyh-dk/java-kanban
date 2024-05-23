package ru.yandex.javacourse.zolotyh.schedule.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacourse.zolotyh.schedule.server.adapter.DurationAdapter;
import ru.yandex.javacourse.zolotyh.schedule.server.adapter.LocalDateTimeAdapter;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.TaskManager;
import ru.yandex.javacourse.zolotyh.schedule.server.handler.*;
import ru.yandex.javacourse.zolotyh.schedule.util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8090; //8080 чем-то занят
    private final HttpServer httpServer;
    private final Gson gson;
    private final TaskManager taskManager;

    public HttpTaskServer() {
        taskManager = Managers.getDefault();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        try {
            httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
            httpServer.createContext("/tasks", new TasksHandler(taskManager, gson));
            httpServer.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
            httpServer.createContext("/epics", new EpicsHandler(taskManager, gson));
            httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
        } catch (IOException e) {
            throw new RuntimeException("Невозможно создать HTTP сервер", e);
        }
    }

    public void start() {
        httpServer.start();
        System.out.println("Сервер на порту " + PORT + " запущен.");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
//        httpTaskServer.stop();
    }
}
