package ru.yandex.javacourse.zolotyh.schedule.util;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.enums.TaskType;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class CSVTaskFormat {

    public static String getHeader() {
        return "id,type,name,status,description,duration,startTime,epicId";
    }

    public static String toString(Task task) {
        return task.getId() + "," + //0
                task.getType() + "," + //1
                task.getName() + "," + //2
                task.getStatus() + "," + //3
                task.getDescription() + "," + //4
                (task.getDuration() == null ? null : task.getDuration().toMinutes()) + "," + //5
                task.getStartTime() + "," + //6
                (task.getType().equals(TaskType.SUBTASK) ? ((Subtask) task).getEpicId() + "," : ""); //7
    }

    //Формат строки: "id,type,name,status,description,duration,startTime,epicId"
    public static Task fromString(String value) {
        final String[] fields = value.split(",");

        final int id = Integer.parseInt(fields[0]);
        final TaskType type = TaskType.valueOf(fields[1]);
        final String name = fields[2];
        final Status status = Status.valueOf(fields[3]);
        final String description = fields[4];
        final Duration duration = fields[5].equals("null") ? null : Duration.ofMinutes(Long.parseLong(fields[5]));
        final LocalDateTime startTime = fields[6].equals("null") ? null : LocalDateTime.parse(fields[6]);

        switch (type) {
            case TASK:
                return new Task(id, name, description, status, duration, startTime);
            case EPIC:
                Epic epic = new Epic(id, name, description);
                epic.setStatus(status);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(fields[7]);
                return new Subtask(id, name, description, status, duration, startTime, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи.");
        }
    }
}
