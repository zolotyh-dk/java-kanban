package ru.yandex.javacourse.zolotyh.schedule.manager.task;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.exception.ManagerSaveException;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;
import ru.yandex.javacourse.zolotyh.schedule.util.CSVTaskFormat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File backupFile;

    public FileBackedTaskManager(File backupFile) {
        this.backupFile = backupFile;
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
        final int id = super.addNewSubtask(subtask);
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
        final int id = super.addNewEpic(epic);
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(backupFile))) {
            writer.write(CSVTaskFormat.getHeader());
            writer.newLine();

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                final Task task = entry.getValue();
                writer.write(CSVTaskFormat.toString(task));
                writer.newLine();
            }

            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                final Task task = entry.getValue();
                writer.write(CSVTaskFormat.toString(task));
                writer.newLine();
            }

            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                final Task task = entry.getValue();
                writer.write(CSVTaskFormat.toString(task));
                writer.newLine();
            }

            writer.newLine();
        } catch (IOException exception) {
            throw new ManagerSaveException("Не удалось сохранить задачи в файл: " + backupFile.getName(), exception);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            final String csv = Files.readString(file.toPath());
            final String[] lines = csv.split(System.lineSeparator());
            int generatorId = 0;
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.isEmpty()) {
                    break;
                }
                final Task task = CSVTaskFormat.fromString(line);
                final int id = task.getId();
                if (id > generatorId) {
                    generatorId = id;
                }
                taskManager.addAnyTask(task);
            }
            for (Map.Entry<Integer, Subtask> entry : taskManager.subtasks.entrySet()) {
                final Subtask subtask = entry.getValue();
                final Epic epic = taskManager.epics.get(subtask.getEpicId());
                epic.addSubtaskId(subtask.getId());
            }
            taskManager.generatorId = generatorId;
        } catch (IOException exception) {
            throw new ManagerSaveException("Не удалось прочитать задачи из файла: " + file.getName(), exception);
        }
        return taskManager;
    }

    public static void main(String[] args) throws IOException {
        File backup = new File("resources/backup.csv");
        FileBackedTaskManager oldManager = new FileBackedTaskManager(backup);
        // Создание задач
        Task task1 = new Task(null, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(null, "Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
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

        // Создание нового менеджера из файла бэкапа
        FileBackedTaskManager newManager = loadFromFile(backup);
        newManager.getAllTasks().forEach(System.out::println);
        newManager.getAllEpics().forEach(System.out::println);
        newManager.getAllSubtasks().forEach(System.out::println);
    }
}
