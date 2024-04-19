package ru.yandex.javacourse.zolotyh.schedule.util;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.enums.TaskType;
import ru.yandex.javacourse.zolotyh.schedule.manager.FileBackedTaskManager;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Deserializer {
    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        String[] lines = content.split("\n");

        for (int i = 1; i < lines.length; i++) {
            Task task = fromString(lines[i]);
            /* Тут можно запомнить старый id и восстановить его после добавления задачи в менеджер.
              Если нужно - добавлю. */
            if (task instanceof Epic) {
                taskManager.addNewEpic((Epic) task);
            } else if (task instanceof Subtask) {
                taskManager.addNewSubtask((Subtask) task);
            } else {
                taskManager.addNewTask(task);
            }
        }

        return taskManager;
    }

    //Формат строки: "id,type,name,status,description,epicId"
    public static Task fromString(String value) {
        final String[] fields = value.split(",");

        final int id = Integer.parseInt(fields[0]);
        final TaskType type = TaskType.valueOf(fields[1]);
        final String name = fields[2];
        final Status status = Status.valueOf(fields[3]);
        final String description = fields[4];

        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description);
            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                return new Subtask(id, name, description, status, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи.");
        }
    }
}
