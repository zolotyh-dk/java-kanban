package ru.yandex.javacourse.zolotyh.schedule.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;
    private Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /task запроса от клиента.");
        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/tasks$", path)) {
                        handleGetAllTasks(httpExchange);
                    }
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        handleGetTask(httpExchange);
                    }
                    break;
                }

                case "PUT": {
                    break;
                }

                case "DELETE": {
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        handleDeleteTask(httpExchange);
                    } else {
                        System.out.println("Ждем DELETE запрос по пути /tasks/{id}, а получили - " + path);
                        httpExchange.sendResponseHeaders(NOT_ALLOWED_405, 0);
                    }
                    break;
                }
                default:
                    System.out.println("Ждем GET, PUT или DELETE запрос, а получили - " + requestMethod);
                    httpExchange.sendResponseHeaders(NOT_ALLOWED_405, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void handleGetAllTasks(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(taskManager.getAllTasks());
        sendText(httpExchange, response);
    }

    private void handleGetTask(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String pathId = path.replaceFirst("/tasks", "");
        int id = parsePathId(pathId);
        if (id != -1) {
            String response = gson.toJson(taskManager.getTaskById(id));
            sendText(httpExchange, response);
        } else {
            System.out.println("Получен некорректный id = " + pathId);
            httpExchange.sendResponseHeaders(NOT_ALLOWED_405, 0);
        }
    }

    private void handleDeleteTask(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String pathId = path.replaceFirst("/tasks", "");
        int id = parsePathId(pathId);
        if (id != -1) {
            taskManager.deleteTask(id);
            System.out.println("Удалили задачу id = " + id);
            httpExchange.sendResponseHeaders(CREATED_201, 0);
        } else {
            System.out.println("Получен некорректный id = " + pathId);
            httpExchange.sendResponseHeaders(405, 0);
        }
    }
}
