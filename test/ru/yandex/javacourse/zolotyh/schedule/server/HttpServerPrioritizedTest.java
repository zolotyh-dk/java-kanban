package ru.yandex.javacourse.zolotyh.schedule.server;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.zolotyh.schedule.server.type_token.TaskListTypeToken;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.javacourse.zolotyh.schedule.server.HttpTaskServer.OK;
import static ru.yandex.javacourse.zolotyh.schedule.util.TaskUtil.getNewTask;

public class HttpServerPrioritizedTest extends AbstractHttpServerTest {
    private static final String URL = "http://localhost:8090/prioritized";

    @Test
    public void getHistoryTest() throws IOException, InterruptedException {
        Task task1 = getNewTask();
        task1.setStartTime(LocalDateTime.now());
        int task1Id = taskManager.addNewTask(task1);

        Task task2 = getNewTask();
        task2.setStartTime(LocalDateTime.now().plusDays(2));
        int task2Id = taskManager.addNewTask(task2);

        Task task3 = getNewTask();
        task3.setStartTime(LocalDateTime.now().plusDays(1));
        int task3Id = taskManager.addNewTask(task3);

        URI uri = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(OK, response.statusCode());
        List<Task> prioritizedTasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(3, prioritizedTasks.size(), "Расзмер списка не совпадает");
        assertEquals(prioritizedTasks.get(0).getId(), task1Id, "Задача 1 должна быть первой в списке приоритетных");
        assertEquals(prioritizedTasks.get(1).getId(), task3Id, "Задача 3 должна быть второй в списке приоритетных");
        assertEquals(prioritizedTasks.get(2).getId(), task2Id, "Задача 2 должна быть третьей в списке приоритетных");
    }
}
