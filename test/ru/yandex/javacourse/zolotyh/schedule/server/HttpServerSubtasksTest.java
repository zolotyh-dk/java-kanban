package ru.yandex.javacourse.zolotyh.schedule.server;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.zolotyh.schedule.server.type_token.SubtaskListTypeToken;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.yandex.javacourse.zolotyh.schedule.server.HttpTaskServer.*;
import static ru.yandex.javacourse.zolotyh.schedule.util.TaskUtil.*;

public class HttpServerSubtasksTest extends AbstractHttpServerTest {
    private static final String URL = "http://localhost:8090/subtasks";

    //GET /subtasks => 200
    @Test
    public void getAllSubtasksTest() throws IOException, InterruptedException {
        Epic epic = getNewEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = getNewSubtask();
        subtask.setEpicId(1);
        taskManager.addNewSubtask(subtask);

        URI uri = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(OK, response.statusCode());
        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        assertNotNull(subtasksFromResponse, "Подзадачи не отправляются сервером");
        assertEquals(1, subtasksFromResponse.size(), "Некорректное количество подзадач");
    }

    //GET /subtasks/{id} => 200
    @Test
    public void getSubtaskSuccessTest() throws IOException, InterruptedException {
        Epic epicAtManager = getNewEpic();
        taskManager.addNewEpic(epicAtManager);
        Subtask subtaskAtManager = getNewSubtask();
        subtaskAtManager.setEpicId(1);
        taskManager.addNewSubtask(subtaskAtManager);

        URI uri = URI.create(URL + "/2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(OK, response.statusCode());
        Subtask subtaskFromResponse = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(subtaskFromResponse, "Подзадача не отправляется сервером");
        assertEquals(subtaskAtManager.getName(), subtaskFromResponse.getName(), "Имя подзадачи не совпадает");
        assertEquals(subtaskAtManager.getDescription(), subtaskFromResponse.getDescription(), "Описание подзадачи не совпадает");
    }

    //GET /subtasks/{id} => 404
    @Test
    public void getSubtaskNotFoundTest() throws IOException, InterruptedException {
        URI uri = URI.create(URL + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(NOT_FOUND, response.statusCode());
    }

    //POST /subtasks => 201
    @Test
    public void addNewSubtaskSuccessTest() throws IOException, InterruptedException {
        Epic epic = getNewEpic();
        taskManager.addNewEpic(epic);

        Subtask subtask = getNewSubtask();
        subtask.setEpicId(1);
        String subtaskJson = gson.toJson(subtask);
        URI uri = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CREATED, response.statusCode());
        List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Новая подзадача", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    //POST /subtasks => 406
    @Test
    public void addNewSubtaskIntersectedTest() throws IOException, InterruptedException {
        Epic epic = getNewEpic();
        taskManager.addNewEpic(epic);

        Subtask subtask1 = getNewSubtask();
        subtask1.setEpicId(1);
        taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = getNewSubtask();
        subtask2.setEpicId(1);
        String subtaskJson = gson.toJson(subtask2);
        URI uri = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(NOT_ACCEPTABLE, response.statusCode());
    }


    //POST /subtasks(с полем id) => 201
    @Test
    public void updateSubtaskSuccessTest() throws IOException, InterruptedException {
        Epic epic = getNewEpic();
        taskManager.addNewEpic(epic);

        Subtask original = getNewSubtask();
        original.setEpicId(1);
        taskManager.addNewSubtask(original);

        Subtask updated = getNewSubtask();
        updated.setEpicId(1);
        updated.setId(original.getId());
        updated.setName("Обновленное имя");

        String updatedSubtaskJson = gson.toJson(updated);
        URI uri = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson))
                .version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CREATED, response.statusCode());
        assertEquals(updated.getName(), taskManager.getSubtaskById(original.getId()).getName(),
                "Несоответствующее имя подзадачи");
    }


    //DELETE /subtasks/{id} => 200
    @Test
    public void deleteSubtaskTest() throws IOException, InterruptedException {
        Epic epic = getNewEpic();
        taskManager.addNewEpic(epic);

        Subtask subtask = getNewSubtask();
        subtask.setEpicId(epic.getId());
        taskManager.addNewSubtask(subtask);

        URI uri = URI.create(URL + "/2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(OK, response.statusCode());
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список подзадач не пустой");
    }
}
