package ru.yandex.javacourse.zolotyh.schedule.manager;

import ru.yandex.javacourse.zolotyh.schedule.exception.ManagerSaveException;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File backup;

    public FileBackedTaskManager(File backup) {
        this.backup = backup;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public int addNewTask(Task task) {
        final int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        final int id =  super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public int addNewEpic(Epic epic) {
        final int id =  super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    private void save() {
        final String heading = "id,type,name,status,description,epic";
        List<Task> all = new ArrayList<>();
        all.addAll(getAllTasks());
        all.addAll(getAllEpics());
        all.addAll(getAllSubtasks());

        try (FileWriter writer = new FileWriter(backup, StandardCharsets.UTF_8)) {
            writer.write(heading + '\n');
            for (int i = 0; i < all.size(); i++) {
                writer.write(all.get(i).toString());
                if (i < all.size() - 1) {
                    writer.write(",\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("При записи в файл возникла ошибка", e);
        }
    }
}
