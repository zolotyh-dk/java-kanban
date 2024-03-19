package ru.yandex.javacourse.zolotyh.schedule.manager;

import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    /*Поменял реализацию на LinkedList из-за лучшей временной сложности удаления первого элемента
    * https://stackoverflow.com/a/30633356*/
    private final List<Task> history = new LinkedList<>();
    private final static int HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (history.size() >= HISTORY_SIZE) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
