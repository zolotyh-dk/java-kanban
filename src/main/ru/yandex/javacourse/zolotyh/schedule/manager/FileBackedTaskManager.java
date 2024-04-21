package ru.yandex.javacourse.zolotyh.schedule.manager;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.enums.TaskType;
import ru.yandex.javacourse.zolotyh.schedule.exception.BackupLoadException;
import ru.yandex.javacourse.zolotyh.schedule.exception.ManagerSaveException;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File backup;

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
        final List<Task> all = new ArrayList<>();
        all.addAll(getAllTasks());
        all.addAll(getAllEpics());
        all.addAll(getAllSubtasks());
        /*Сортируем лист чтобы объекты в файле хранились в порядке возрастания id
        * Таким образом, при восстановлении хранилища, для всех задач сгенерируются те же самые id*/
        Collections.sort(all);

        try (FileWriter writer = new FileWriter(backup, StandardCharsets.UTF_8)) {
            writer.write(heading + '\n');
            for (int i = 0; i < all.size(); i++) {
                writer.write(all.get(i).toString());
                if (i < all.size() - 1) {
                    writer.write(",\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить задачи в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        String content;
        try {
            content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BackupLoadException("Не удалось прочитать файл бэкапа", e);
        }
        String[] lines = content.split("\n");

        for (int i = 1; i < lines.length; i++) {
            Task task = fromString(lines[i]);
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
    private static Task fromString(String value) {
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
