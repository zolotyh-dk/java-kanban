package ru.yandex.javacourse.zolotyh.schedule.util;

import ru.yandex.javacourse.zolotyh.schedule.manager.HistoryManager;
import ru.yandex.javacourse.zolotyh.schedule.manager.InMemoryHistoryManager;
import ru.yandex.javacourse.zolotyh.schedule.manager.InMemoryTaskManager;
import ru.yandex.javacourse.zolotyh.schedule.manager.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
