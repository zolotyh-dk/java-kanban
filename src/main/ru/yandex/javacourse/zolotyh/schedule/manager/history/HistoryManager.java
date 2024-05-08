package ru.yandex.javacourse.zolotyh.schedule.manager.history;

import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
