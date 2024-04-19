package ru.yandex.javacourse.zolotyh.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    private TaskManager taskManager;
    private Path backup;
    private static final String SERIALIZED_SUBTASK = "2,SUBTASK,Новая подзадача,NEW,Описание подзадачи,1";
    private static final String HEADING = "id,type,name,status,description,epic";

    @BeforeEach
    public void beforeEach() throws IOException {
        backup = Files.createTempFile(Paths.get("test_resources"), "backup_test", ".csv");
        taskManager = new FileBackedTaskManager(backup.toFile());
    }

    //Тесты на добавление новых объектов ⬇️

    //"id,type,name,status,description,epic"
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
}
