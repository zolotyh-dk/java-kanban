package ru.yandex.javacourse.zolotyh.schedule.manager;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.exception.ManagerSaveException;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;
import ru.yandex.javacourse.zolotyh.schedule.util.Deserializer;

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

    public static void main(String[] args) {
        File backup = new File("resources/backup.csv");
        FileBackedTaskManager oldManager = new FileBackedTaskManager(backup);
        // Создание задач
        Task task1 = new Task(null, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(null, "Задача 2", "Описание задачи 2", Status.IN_PROGRESS);

        // Добавление задач в менеджер истории
        oldManager.addNewTask(task1);
        oldManager.addNewTask(task2);

        // Создание эпика c тремя подзадачами
        Epic epic = new Epic(null, "Эпик с тремя подзадачами", "Описание эпика с тремя подзадачами");
        oldManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask(null, "Подзадача 1", "Описание подзадачи 1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask(null, "Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, epic.getId());
        Subtask subtask3 = new Subtask(null, "Подзадача 3", "Описание подзадачи 3", Status.DONE, epic.getId());
        oldManager.addNewSubtask(subtask1);
        oldManager.addNewSubtask(subtask2);
        oldManager.addNewSubtask(subtask3);

        // Создание эпика без подзадач
        Epic epicWithoutSubtasks = new Epic(null, "Эпик без подзадач", "Описание эпика без подзадач");
        oldManager.addNewEpic(epicWithoutSubtasks);

        FileBackedTaskManager newManager = Deserializer.loadFromFile(backup);
        newManager.getAllTasks().forEach(System.out::println);
        newManager.getAllEpics().forEach(System.out::println);
        newManager.getAllSubtasks().forEach(System.out::println);
    }
}
