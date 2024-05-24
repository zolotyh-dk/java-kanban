package ru.yandex.javacourse.zolotyh.schedule.server;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.zolotyh.schedule.server.type_token.TaskListTypeToken;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.javacourse.zolotyh.schedule.server.HttpTaskServer.OK;
import static ru.yandex.javacourse.zolotyh.schedule.util.TaskUtil.*;

public class HttpServerHistoryTest extends AbstractHttpServerTest {
    private static final String URL = "http://localhost:8090/history";

    @Test
    public void getHistoryTest() throws IOException, InterruptedException {
        Task task = getNewTask();
        int taskId = taskManager.addNewTask(task);

        Epic epic = getNewEpic();
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = getNewSubtask();
        subtask.setEpicId(epicId);
        int subtaskId = taskManager.addNewSubtask(subtask);

        taskManager.getEpicById(epicId);
        taskManager.getSubtaskById(subtaskId);
        taskManager.getTaskById(taskId);

        URI uri = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(OK, response.statusCode());
        List<Task> history = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(history.get(0).getId(), epicId, "Эпик должен быть первым в истории просмотров");
        assertEquals(history.get(1).getId(), subtaskId, "Подзадача должна быть первой в истории просмотров");
        assertEquals(history.get(2).getId(), taskId, "Задача должна быть третьей в истории просмотров");
    }
}
