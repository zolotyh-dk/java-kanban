package ru.yandex.javacourse.zolotyh.schedule.util;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public class TaskUtil {
    public static List<Task> getTestTasks() {
        return List.of(
                new Task(1, "Задача 1", "Описание задачи 1", Status.NEW,
                        Duration.ofMinutes(30), LocalDateTime.of(2024, Month.MAY, 1, 8, 0)),
                new Task(2, "Задача 2", "Описание задачи 2", Status.IN_PROGRESS,
                        Duration.ofMinutes(45), LocalDateTime.of(2024, Month.MAY, 1, 12, 15)),
                new Task(3, "Задача 3", "Описание задачи 3", Status.DONE,
                        Duration.ofMinutes(60), LocalDateTime.of(2024, Month.MAY, 3, 10, 0))
        );
    }

    public static List<Epic> getTestEpics() {
        return List.of(
                new Epic(4, "Эпик с тремя подзадачами", "Описание эпика с тремя подзадачами"),
                new Epic(5, "Эпик без подзадач", "Описание эпика без подзадач")
        );
    }

    public static List<Subtask> getTestSubtasks() {
        return List.of(
                new Subtask(6, "Подзадача 1", "Описание подзадачи 1", Status.NEW,
                        Duration.ofMinutes(90), LocalDateTime.of(2024, Month.MAY, 2, 11, 0), 4),
                new Subtask(7, "Подзадача 2", "Описание подзадачи 2", Status.DONE,
                        Duration.ofMinutes(120), LocalDateTime.of(2024, Month.MAY, 1, 10, 0), 4),
                new Subtask(8, "Подзадача 3", "Описание подзадачи 3", Status.DONE,
                        Duration.ofMinutes(15), LocalDateTime.of(2024, Month.MAY, 3, 16, 0), 4)
        );
    }

    public static Task getNewTask() {
        return new Task(null, "Новая задача", "Описание задачи", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2024, Month.JUNE, 1, 8, 0));
    }

    public static Epic getNewEpic() {
        return new Epic(null, "Новый эпик", "Описание нового эпика");
    }

    public static Subtask getNewSubtask() {
        return new Subtask(null, "Новая подзадача", "Описание подзадачи", Status.DONE,
                Duration.ofMinutes(15), LocalDateTime.of(2024, Month.JUNE, 1, 9, 0), 4);
    }
}
