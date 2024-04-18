package ru.yandex.javacourse.zolotyh.schedule.util;

import ru.yandex.javacourse.zolotyh.schedule.manager.*;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
//        return new InMemoryTaskManager();
        return new FileBackedTaskManager(new File("resources/backup.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
