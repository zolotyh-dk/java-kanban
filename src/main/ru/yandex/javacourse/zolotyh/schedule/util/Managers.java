package ru.yandex.javacourse.zolotyh.schedule.util;

import ru.yandex.javacourse.zolotyh.schedule.manager.history.HistoryManager;
import ru.yandex.javacourse.zolotyh.schedule.manager.history.InMemoryHistoryManager;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.FileBackedTaskManager;
import ru.yandex.javacourse.zolotyh.schedule.manager.task.TaskManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("resources/backup.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
