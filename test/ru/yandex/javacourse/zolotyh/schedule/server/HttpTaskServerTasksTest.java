package ru.yandex.javacourse.zolotyh.schedule.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.InMemoryTaskManager;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.TaskManager;
import ru.yandex.javacourse.zolotyh.schedule.server.type_token.TaskListTypeToken;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.javacourse.zolotyh.schedule.server.HttpTaskServer.*;
import static ru.yandex.javacourse.zolotyh.schedule.util.TaskUtil.getNewTask;
import static ru.yandex.javacourse.zolotyh.schedule.util.TaskUtil.getTestTasks;

class HttpTaskServerTasksTest {
    private static final String URL = "http://localhost:8090/tasks";
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = taskServer.getGson();
    HttpClient client;



    @BeforeEach
    void setUp() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
        client.close();
    }

    //GET /tasks => 200
    @Test
    public void getAllTasksSuccessTest() throws IOException, InterruptedException {
        getTestTasks().forEach(taskManager::addNewTask);
        URI uri = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(OK, response.statusCode());
        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertNotNull(tasksFromResponse, "Задачи не отправляются сервером");
        assertEquals(3, tasksFromResponse.size(), "Некорректное количество задач");
    }

    //GET /tasks{id} => 200
    @Test
    public void getTaskSuccessTest() throws IOException, InterruptedException {
        Task taskAtManager = getNewTask();
        taskManager.addNewTask(taskAtManager);
        URI uri = URI.create(URL + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(OK, response.statusCode());
        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        assertNotNull(taskFromResponse, "Задача не отправляется сервером");
        assertEquals(taskAtManager.getName(), taskFromResponse.getName());
        assertEquals(taskAtManager.getDescription(), taskFromResponse.getDescription());
        assertEquals(taskAtManager.getStatus(), taskFromResponse.getStatus());
        assertEquals(taskAtManager.getStartTime(), taskFromResponse.getStartTime());
        assertEquals(taskAtManager.getDuration(), taskFromResponse.getDuration());
    }

    //GET /tasks/{id} => 404
    @Test
    public void getTaskNotFoundTest() throws IOException, InterruptedException {
        URI uri = URI.create(URL + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(NOT_FOUND, response.statusCode());
    }

    //POST /tasks => 201
    @Test
    public void addNewTaskTest() throws IOException, InterruptedException {
        Task task = getNewTask();
        String taskJson = gson.toJson(task);
        URI uri = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CREATED, response.statusCode());
        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Новая задача", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    //POST /tasks => 406
    @Test
    public void addNewTaskIntersectionTest() throws IOException, InterruptedException {
        Task newTask = getNewTask();
        taskManager.addNewTask(newTask);

        Task taskAtTheSameTime = getNewTask();
        String taskAtTheSameTimeJson = gson.toJson(taskAtTheSameTime);
        URI uri = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskAtTheSameTimeJson))
                .version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(NOT_ACCEPTABLE, response.statusCode());
    }

    //POST /task(с полем id) => 201
    @Test
    public void updateTaskTest() throws IOException, InterruptedException {
        Task task = getNewTask();
        taskManager.addNewTask(task);

        Task updatedTask = new Task(1, "Имя обновленной задачи", "Описание обновленной задачи",
                Status.IN_PROGRESS);
        String updatedTaskJson = gson.toJson(updatedTask);
        URI uri = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CREATED, response.statusCode());
        assertEquals(updatedTask.getName(), taskManager.getTaskById(1).getName(), "Несоответствующее имя задачи");
    }

    //POST /tasks => 406
    @Test
    public void updateTaskIntersectionTest() throws IOException, InterruptedException {
        Task nowTask = new Task(null, "Задача прямо сейчас", "Описание задачи", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        Task farFutureTask = new Task(null, "Задача в далеком будущем", "Описание", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.MAX.minusYears(1));
        taskManager.addNewTask(nowTask);
        taskManager.addNewTask(farFutureTask);

        farFutureTask.setStartTime(LocalDateTime.now()); //Обновляем время задачи из будущего на сейчас (оно уже занято)
        String farFutureTaskJson = gson.toJson(farFutureTask);
        URI uri = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(farFutureTaskJson))
                .version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(NOT_ACCEPTABLE, response.statusCode());
    }

    //DELETE /tasks => 200
    @Test
    public void deleteTaskTest() throws IOException, InterruptedException {
        Task task = getNewTask();
        taskManager.addNewTask(task);

        URI uri = URI.create(URL + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(OK, response.statusCode());
        assertEquals(0, taskManager.getAllTasks().size(), "Список задач не пустой");
    }
}