package ru.yandex.javacourse.zolotyh.schedule.util;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
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
                        Duration.ofMinutes(30), LocalDateTime.of(2024, Month.MAY, 3, 16, 0), 4)
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

    public static List<Task> getTasksWithTimeIntersection() {
        return List.of(
                // 03.05 16:00 - 16:30             [------Подзадача 3------]
                // 03.05 15:30 - 16:15  [------Эта задача------]
                new Task(null, "Задача, пересекающаяся по времени с подзадачей 3", "Описание", Status.NEW,
                        Duration.ofMinutes(45), LocalDateTime.of(2024, Month.MAY, 3, 15, 30)),

                // 01.05 08:00 - 08:30  [------Задача 1------]
                // 01.05 08:15 - 09:00             [------Эта задача------]
                new Task(null, "Задача, пересекающаяся по времени с задачей 1", "Описание", Status.NEW,
                        Duration.ofMinutes(45), LocalDateTime.of(2024, Month.MAY, 1, 8, 15)),

                // 01.05 12:15 - 13:00  [------Задача 2------]
                // 01.05 12:15 - 13:00  [------Эта задача----]
                new Task(null, "Задача, полностью совпадающая по времени с задачей 2", "Описание", Status.NEW,
                        Duration.ofMinutes(45), LocalDateTime.of(2024, Month.MAY, 1, 12, 15)),

                // 01.05 12:15 - 13:00  [------Задача 2------]
                // 01.05 12:30 - 12:45     [--Эта задача--]
                new Task(null, "Задача, внутри интервала задачи 2", "Описание", Status.NEW,
                        Duration.ofMinutes(15), LocalDateTime.of(2024, Month.MAY, 1, 12, 30)),

                // 02.05 11:00 - 12:30      [--Подзадача 1--]
                // 02.05 10:00 - 14:00  [------Эта задача-------]
                new Task(null, "Задача, полностью перекрывающая подзадачу 1", "Описание", Status.NEW,
                        Duration.ofMinutes(240), LocalDateTime.of(2024, Month.MAY, 2, 10, 0))
        );
    }

    public static List<Subtask> generateSubtasksWithStatus(int newNumber, int inProgressNumber, int doneNumber, int epicId) {
        List<Subtask> subtasks = new ArrayList<>();
        for (int i = 0; i < newNumber; i++) {
            subtasks.add(new Subtask(null, "Подзадача со статусом NEW №" + (i + 1), "Описание",
                    Status.NEW, epicId));
        }
        for (int i = 0; i < inProgressNumber; i++) {
            subtasks.add(new Subtask(null, "Подзадача со статусом IN_PROGRESS №" + (i + 1), "Описание",
                    Status.IN_PROGRESS, epicId));
        }
        for (int i = 0; i < doneNumber; i++) {
            subtasks.add(new Subtask(null, "Подзадача со статусом DONE №" + (i + 1), "Описание",
                    Status.DONE, epicId));
        }
        return subtasks;
    }

    public static List<String> getTestTasksAsStrings() {
        return List.of(
                "1,TASK,Задача 1,NEW,Описание задачи 1,30,2024-05-01T08:00,",
                "2,TASK,Задача 2,IN_PROGRESS,Описание задачи 2,45,2024-05-01T12:15,",
                "3,TASK,Задача 3,DONE,Описание задачи 3,60,2024-05-03T10:00,",
                "6,SUBTASK,Подзадача 1,NEW,Описание подзадачи 1,90,2024-05-02T11:00,4,",
                "7,SUBTASK,Подзадача 2,DONE,Описание подзадачи 2,120,2024-05-01T10:00,4,",
                "8,SUBTASK,Подзадача 3,DONE,Описание подзадачи 3,30,2024-05-03T16:00,4,",
                "4,EPIC,Эпик с тремя подзадачами,IN_PROGRESS,Описание эпика с тремя подзадачами,240,2024-05-01T10:00,",
                "5,EPIC,Эпик без подзадач,NEW,Описание эпика без подзадач,null,null,"
        );
    }
}
