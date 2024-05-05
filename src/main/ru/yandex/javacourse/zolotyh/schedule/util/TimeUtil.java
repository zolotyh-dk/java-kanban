package ru.yandex.javacourse.zolotyh.schedule.util;

import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.util.List;

public class TimeUtil {
    public static boolean isTimeIntersections(Task newTask, List<Task> existedTasks) {
        return existedTasks.stream().anyMatch(existedTask -> TimeUtil.isTimeIntersection(newTask, existedTask));
    }

    public static boolean isTimeIntersection(Task newTask, Task existedTask) {
        return newTask.getStartTime().isBefore(existedTask.getEndTime()) &&
                newTask.getEndTime().isAfter(existedTask.getStartTime());
    }
}