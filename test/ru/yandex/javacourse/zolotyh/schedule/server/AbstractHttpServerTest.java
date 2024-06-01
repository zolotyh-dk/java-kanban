package ru.yandex.javacourse.zolotyh.schedule.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.InMemoryTaskManager;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.TaskManager;

import java.net.http.HttpClient;

public abstract class AbstractHttpServerTest {
    protected final TaskManager taskManager = new InMemoryTaskManager();
    protected final HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    protected final Gson gson = taskServer.getGson();
    protected HttpClient client;

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
}
