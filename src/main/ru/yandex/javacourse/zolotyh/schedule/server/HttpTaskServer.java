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
    private final HttpServer httpServer;
    private final Gson gson;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
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

    public Gson getGson() {
        return gson;
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        httpTaskServer.start();
//        httpTaskServer.stop();
    }
}
