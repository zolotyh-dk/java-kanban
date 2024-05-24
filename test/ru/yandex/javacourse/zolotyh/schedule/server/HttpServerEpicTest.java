package ru.yandex.javacourse.zolotyh.schedule.server;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.server.type_token.EpicListTypeToken;
import ru.yandex.javacourse.zolotyh.schedule.server.type_token.SubtaskListTypeToken;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.yandex.javacourse.zolotyh.schedule.server.HttpTaskServer.*;
import static ru.yandex.javacourse.zolotyh.schedule.util.TaskUtil.getNewEpic;
import static ru.yandex.javacourse.zolotyh.schedule.util.TaskUtil.getTestEpics;

public class HttpServerEpicTest extends AbstractHttpServerTest {
    private static final String URL = "http://localhost:8090/epics";

    //GET /epics => 200
    @Test
    public void getAllEpicsTest() throws IOException, InterruptedException {
        getTestEpics().forEach(taskManager::addNewEpic);
        URI uri = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(OK, response.statusCode());
        List<Epic> epicsFromResponse = gson.fromJson(response.body(), new EpicListTypeToken().getType());
        assertNotNull(epicsFromResponse, "Эпики не отправляются сервером");
        assertEquals(2, epicsFromResponse.size(), "Некорректное количество эпиков");
    }

    //GET /epics/{id} => 200
    @Test
    public void getEpicSuccessTest() throws IOException, InterruptedException {
        Epic epicAtManager = getNewEpic();
        taskManager.addNewEpic(epicAtManager);
        URI uri = URI.create(URL + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(OK, response.statusCode());
        Epic epicFromResponse = gson.fromJson(response.body(), Epic.class);
        assertNotNull(epicFromResponse, "Эпик не отправляется сервером");
        assertEquals(epicAtManager.getName(), epicFromResponse.getName());
        assertEquals(epicAtManager.getDescription(), epicFromResponse.getDescription());
    }

    //GET /epics/{id} => 404
    @Test
    public void getEpicNotFoundTest() throws IOException, InterruptedException {
        URI uri = URI.create(URL + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(NOT_FOUND, response.statusCode());
    }

    //GET /epic/{id}/subtasks => 200
    @Test
    public void getSubtasksByEpicSuccessTest() throws IOException, InterruptedException {
        Epic epic = new Epic(null, "Эпик с тремя подзадачами", "Описание эпика с тремя подзадачами");
        Subtask subtask1 = new Subtask(null, "Подзадача 1", "Описание подзадачи 1", Status.NEW,
                        Duration.ofMinutes(90), LocalDateTime.of(2024, Month.MAY, 1, 11, 0), 1);
        Subtask subtask2 = new Subtask(7, "Подзадача 2", "Описание подзадачи 2", Status.DONE,
                        Duration.ofMinutes(120), LocalDateTime.of(2024, Month.MAY, 2, 10, 0), 1);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        URI uri = URI.create(URL + "/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(OK, response.statusCode());
        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        assertNotNull(subtasksFromResponse, "Подзадачи не отправляются сервером");
        assertEquals(2, subtasksFromResponse.size(), "Некорректное количество подзадач");
    }

    //GET /epic/{id}/subtasks => 404
    @Test
    public void getSubtasksByEpicNotFoundTest() throws IOException, InterruptedException {
        URI uri = URI.create(URL + "/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(NOT_FOUND, response.statusCode());
    }

    //POST /epics => 201
    @Test
    public void addNewEpicTest() throws IOException, InterruptedException {
        Epic epic = getNewEpic();
        String epicJson = gson.toJson(epic);
        URI uri = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CREATED, response.statusCode());
        List<Epic> epicsFromManager = taskManager.getAllEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Новый эпик", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }


    //POST /epics(с полем id) => 201
    @Test
    public void updateEpicTest() throws IOException, InterruptedException {
        Epic epic = getNewEpic();
        taskManager.addNewEpic(epic);

        Epic updatedEpic = new Epic(1, "Имя обновленного эпика", "Описание обновленного эпика");
        String updatedEpicJson = gson.toJson(updatedEpic);
        URI uri = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(updatedEpicJson))
                .version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CREATED, response.statusCode());
        assertEquals(updatedEpic.getName(), taskManager.getEpicById(1).getName(), "Несоответствующее имя эпика");
    }


    //DELETE /epics/{id} => 200
    @Test
    public void deleteTaskTest() throws IOException, InterruptedException {
        Epic epic = getNewEpic();
        taskManager.addNewEpic(epic);

        URI uri = URI.create(URL + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(OK, response.statusCode());
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков не пустой");
    }
}
