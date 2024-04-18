package ru.yandex.javacourse.zolotyh.schedule.util;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.enums.TaskType;
import ru.yandex.javacourse.zolotyh.schedule.exception.ManagerSaveException;
import ru.yandex.javacourse.zolotyh.schedule.manager.FileBackedTaskManager;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        String content = "";

        try {
            content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке задач из файла", e);
        }

        String[] lines = content.split("\n");
        for (int i = 1; i < lines.length; i++) {
            Task task = fromString(lines[i]);
            if (task instanceof Epic) {
                fileBackedTaskManager.addNewEpic((Epic) task);
            } else if (task instanceof Subtask) {
                fileBackedTaskManager.addNewSubtask((Subtask) task);
            } else {
                fileBackedTaskManager.addNewTask(task);
            }
        }

        return fileBackedTaskManager;
    }
}
