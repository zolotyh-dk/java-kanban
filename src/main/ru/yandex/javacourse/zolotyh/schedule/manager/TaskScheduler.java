package ru.yandex.javacourse.zolotyh.schedule.manager;

import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TaskScheduler {
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime)
            .thenComparing(Task::getId));

    /* Время задачи newTask:     A-----------B
       Отрезок задачи savedTask:      C-----------D

       Условие 1: A < D
       Условие 2: С < B */
    private boolean checkForIntersection(Task newTask, List<Task> savedTasks) {
        return savedTasks.stream()
                .anyMatch(savedTask -> newTask.getStartTime().isBefore(savedTask.getEndTime())
                        && savedTask.getStartTime().isBefore(newTask.getEndTime()));
    }
}
