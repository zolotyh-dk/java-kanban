package ru.yandex.javacourse.zolotyh.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.exception.ManagerSaveException;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private TaskManager taskManager;
    private Path backup;

    @BeforeEach
    public void beforeEach() throws IOException {
        backup = Files.createTempFile(Paths.get("."), "backup_test", ".csv");
        taskManager = new FileBackedTaskManager(backup.toFile());
        backup.toFile().deleteOnExit();
    }

    //Тесты на добавление новых объектов ⬇️
    @Test
    public void addNewTask() throws IOException {
        // Создаем новую задачу
        final Task task = new Task(null, "Новая задача", "Описание задачи", Status.NEW);
        taskManager.addNewTask(task);

        // Считываем задачи из файла
        final String content = Files.readString(backup);
        final String[] lines = content.split("\n");

        // Проверяем, что количество строк в файле и строка задачи соответствуют ожидаемым.
        assertEquals(2, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("1,TASK,Новая задача,NEW,Описание задачи", lines[1],
                "Сериализованная задача в файле не совпадает.");
    }

    @Test
    public void addNewEpicWithSubtask() throws IOException {
        // Создаем новый эпик
        final Epic epic = new Epic(null, "Новый эпик", "Описание эпика");
        final int epicId = taskManager.addNewEpic(epic);

        // Создаем новую подзадачу и добавляем ее к эпику
        final Subtask subtask = new Subtask(null, "Новая подзадача", "Описание подзадачи",
                Status.NEW, epicId);
        taskManager.addNewSubtask(subtask);

        // Считываем задачи из файла
        final String content = Files.readString(backup);
        final String[] lines = content.split("\n");

        // Проверяем, что количество строк в файле и строка задачи соответствуют ожидаемым.
        assertEquals(3, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("1,EPIC,Новый эпик,NEW,Описание эпика,", lines[1],
                "Сериализованый эпик в файле не совпадает.");
        assertEquals("2,SUBTASK,Новая подзадача,NEW,Описание подзадачи,1", lines[2],
                "Сериализованая подзадача в файле не совпадает.");
    }

    //Тесты на обновление существующих объектов ⬇️
    @Test
    public void updateTask() throws IOException {
        // Создаем и сохраняем старую задачу
        final Task old = new Task(null, "Старая задача", "Описание старой задачи", Status.NEW);
        final int id = taskManager.addNewTask(old);

        // Создаем и сохраняем обновленную задачу
        final Task updated = new Task(id, "Новая задача", "Описание новой задачи", Status.IN_PROGRESS);
        taskManager.updateTask(updated);

        // Считываем задачи из файла
        final String content = Files.readString(backup);
        final String[] lines = content.split("\n");

        // Проверяем, что задача успешно обновлена в файле
        assertEquals(2, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("1,TASK,Новая задача,IN_PROGRESS,Описание новой задачи", lines[1],
                "Сериализованная задача в файле не совпадает.");
    }

    @Test
    public void updateEpic() throws IOException {
        // Создаем и сохраняем старый эпик
        final Epic old = new Epic(null, "Старый эпик", "Описание старого эпика");
        final int id = taskManager.addNewEpic(old);

        // Создаем и сохраняем обновленный эпик
        final Epic updated = new Epic(id, "Новый эпик", "Описание нового эпика");
        taskManager.updateEpic(updated);

        // Считываем задачи из файла
        final String content = Files.readString(backup);
        final String[] lines = content.split("\n");

        // Проверяем, что задача успешно обновлена в файле
        assertEquals(2, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("1,EPIC,Новый эпик,NEW,Описание нового эпика", lines[1],
                "Сериализованный эпик в файле не совпадает.");
    }

    @Test
    public void updateSubtask() throws IOException {
        // Создаем эпик и сохраняем его
        final Epic epic = new Epic(null, "Новый эпик", "Описание нового эпика");
        final int epicId = taskManager.addNewEpic(epic);

        // Создаем и сохраняем старую подзадачу, привязанную к эпику
        final Subtask old = new Subtask(null, "Старая подзадача", "Описание старой подзадачи",
                Status.NEW, epicId);
        final int subtaskId = taskManager.addNewSubtask(old);

        // Создаем и сохраняем обновленную подзадачу, привязанную к эпику
        final Subtask updated = new Subtask(subtaskId, "Обновленная подзадача",
                "Описание обновленной подзадачи", Status.IN_PROGRESS, epicId);
        taskManager.updateSubtask(updated);

        // Считываем задачи из файла
        final String content = Files.readString(backup);
        final String[] lines = content.split("\n");

        // Проверяем, что подзадача успешно обновлена в файле и статус эпика изменен
        assertEquals(3, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("1,EPIC,Новый эпик,IN_PROGRESS,Описание нового эпика,", lines[1],
                "Сериализованный эпик в файле не совпадает.");
        assertEquals("2,SUBTASK,Обновленная подзадача,IN_PROGRESS,Описание обновленной подзадачи,1", lines[2],
                "Сериализованная подзадача в файле не совпадает.");
    }

    //Тесты на удаление объектов ⬇️
    @Test
    public void deleteTask() throws IOException {
        // Создаем и сохраняем задачу
        final Task task = new Task(null, "Новая задача", "Описание задчи", Status.NEW);
        final int id = taskManager.addNewTask(task);

        // Удаляем задачу
        taskManager.deleteTask(id);

        // Считываем задачи из файла
        final String content = Files.readString(backup);
        final String[] lines = content.split("\n");

        // Проверяем, что задача успешно удалена из файла и в файле только строка заголовков
        assertEquals(1, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("id,type,name,status,description,epic", lines[0],
                "Строка заголовоков не совпадает.");
    }

    @Test
    public void deleteEpic() throws IOException {
        // Создаем новый эпик и связанную с ним подзадачу
        final Epic epic = new Epic(null, "Новый эпик", "Описание эпика");
        final int epicId = taskManager.addNewEpic(epic);
        final Subtask subtask = new Subtask(null, "Новая подзадача",
                "Описание подзадачи", Status.NEW, epicId);
        taskManager.addNewSubtask(subtask);

        // Удаляем эпик
        taskManager.deleteEpic(epicId);

        // Считываем задачи из файла
        final String content = Files.readString(backup);
        final String[] lines = content.split("\n");

        // Проверяем, что эпик успешно удален из файла и в файле только строка заголовков
        assertEquals(1, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("id,type,name,status,description,epic", lines[0],
                "Строка заголовоков не совпадает.");
    }

    @Test
    public void deleteSubtask() throws IOException {
        // Создаем новый эпик и связанную с ним подзадачу
        final Epic epic = new Epic(null, "Новый эпик", "Описание эпика");
        final int epicId = taskManager.addNewEpic(epic);
        final Subtask subtask = new Subtask(null, "Новая подзадача", "Описание подзадачи",
                Status.DONE, epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        // Удаляем подзадачу
        taskManager.deleteSubtask(subtaskId);

        // Считываем задачи из файла
        final String content = Files.readString(backup);
        final String[] lines = content.split("\n");

        // Проверяем, что подзадача и её эпик успешно удалены из файла и в файле только строка заголовков
        assertEquals(1, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("id,type,name,status,description,epic", lines[0],
                "Строка заголовоков не совпадает.");
    }

    @Test
    public void deleteAllTasks() throws IOException {
        // Создаем и сохраняем несколько задач
        final Task task1 = new Task(null, "Задача 1", "Описание задчи 1", Status.NEW);
        final Task task2 = new Task(null, "Задача 2", "Описание задчи 2", Status.IN_PROGRESS);
        final int task1Id = taskManager.addNewTask(task1);
        final int task2Id = taskManager.addNewTask(task2);

        // Удаляем все задачи
        taskManager.deleteAllTasks();

        // Считываем задачи из файла
        final String content = Files.readString(backup);
        final String[] lines = content.split("\n");

        // Проверяем, что задача успешно удалена из файла и в файле только строка заголовков
        assertEquals(1, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("id,type,name,status,description,epic", lines[0],
                "Строка заголовоков не совпадает.");
    }

    @Test
    public void deleteAllSubtasks() throws IOException {
        // Создаем и сохраняем несколько эпиков и подзадач
        final Epic epic1 = new Epic(null, "Эпик 1", "Описание эпика 1");
        final Epic epic2 = new Epic(null, "Эпик 2", "Описание эпика 2");
        final int epic1Id = taskManager.addNewEpic(epic1);
        final int epic2Id = taskManager.addNewEpic(epic2);
        final Subtask subtask1 = new Subtask(null, "Подзадача эпика 1", "Описание подзадачи эпика 1",
                Status.DONE, epic1Id);
        final Subtask subtask2 = new Subtask(null, "Подзадача эпика 2", "Описание подзадачи эпика 2",
                Status.NEW, epic2Id);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        // Удаляем все подзадачи
        taskManager.deleteAllSubtasks();

        // Считываем задачи из файла
        final String content = Files.readString(backup);
        final String[] lines = content.split("\n");

        // Проверяем, что подзадачи успешно удалена из файла, а 2 эпика остались
        assertEquals(3, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("1,EPIC,Эпик 1,NEW,Описание эпика 1,", lines[1],
                "Сериализованый эпик 1 в файле не совпадает.");
        assertEquals("2,EPIC,Эпик 2,NEW,Описание эпика 2", lines[2],
                "Сериализованый эпик 2 в файле не совпадает.");
    }

    @Test
    public void deleteAllEpics() throws IOException {
        // Создаем и сохраняем несколько эпиков и связанные с ними подзадачи
        final Epic epic1 = new Epic(null, "Эпик 1", "Описание эпика 1");
        final Epic epic2 = new Epic(null, "Эпик 2", "Описание эпика 2");
        final int epic1Id = taskManager.addNewEpic(epic1);
        final int epic2Id = taskManager.addNewEpic(epic2);
        final Subtask subtask1 = new Subtask(null, "Подзадача эпика 1", "Описание подзадачи эпика 1",
                Status.DONE, epic1Id);
        final Subtask subtask2 = new Subtask(null, "Подзадача эпика 2", "Описание подзадачи эпика 2",
                Status.NEW, epic2Id);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        // Удаляем все эпики
        taskManager.deleteAllEpics();

        // Считываем задачи из файла
        final String content = Files.readString(backup);
        final String[] lines = content.split("\n");

        // Проверяем, что подзадачи и эпики успешно удалены из файла, а в файле осталась строка заголовков
        assertEquals(1, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("id,type,name,status,description,epic", lines[0],
                "Строка заголовоков не совпадает.");
    }

    //Тесты на сохранение задач в файл по несуществующему пути ⬇️
    @Test
    public void shouldThrowsManagerSaveException() {
        // Создаем менеджер задач и передаем ему файл по несуществующему пути
        File wrong = new File("nonexistent_path/backup.csv");
        taskManager = new FileBackedTaskManager(wrong);

        // Создаем новую задачу
        final Task task = new Task(null, "Новая задача", "Описание задачи", Status.NEW);

        // Проверяем что вызов метода сохранения задачи выбрасывает ManagerSaveException
        assertThrows(ManagerSaveException.class, () -> taskManager.addNewTask(task));
    }
}
