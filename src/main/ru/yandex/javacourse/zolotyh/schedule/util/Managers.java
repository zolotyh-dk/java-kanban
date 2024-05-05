package ru.yandex.javacourse.zolotyh.schedule.util;

import ru.yandex.javacourse.zolotyh.schedule.manager.history_manager.HistoryManager;
import ru.yandex.javacourse.zolotyh.schedule.manager.history_manager.InMemoryHistoryManager;
import ru.yandex.javacourse.zolotyh.schedule.manager.task_manager.FileBackedTaskManager;
import ru.yandex.javacourse.zolotyh.schedule.manager.task_manager.TaskManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("resources/backup.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
