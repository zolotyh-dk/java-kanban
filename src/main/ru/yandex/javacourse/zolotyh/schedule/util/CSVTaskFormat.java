package ru.yandex.javacourse.zolotyh.schedule.util;

import ru.yandex.javacourse.zolotyh.schedule.enums.TaskType;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

public class CSVTaskFormat {
    public static String getHeader() {
        return "id,type,name,status,description,epic";
    }

    public static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," +
                task.getStatus() + "," + task.getDescription() + "," +
                (task.getType().equals(TaskType.SUBTASK) ? ((Subtask) task).getEpicId() : "");
    }
}
