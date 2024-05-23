package ru.yandex.javacourse.zolotyh.schedule.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.InMemoryTaskManager;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.TaskManager;

class HttpTaskServerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = taskServer.getGson();


    @BeforeEach
    void setUp() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }
}