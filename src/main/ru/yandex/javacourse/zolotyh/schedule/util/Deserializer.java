package ru.yandex.javacourse.zolotyh.schedule.util;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.enums.TaskType;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

public class Deserializer {

    //Формат строки: "id,type,name,status,description,epic"
    public static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        String description = fields[4];

        switch (type) {
            case TASK:
                Status taskStatus = Status.valueOf(fields[3]);
                return new Task(id, name, description, taskStatus);
            case EPIC:
                return new Epic(id, name, description);
            case SUBTASK:
                Status subtaskStatus = Status.valueOf(fields[3]);
                int epicId = Integer.parseInt(fields[5]);
                return new Subtask(id, name, description, subtaskStatus, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }
}
