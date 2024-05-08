package ru.yandex.javacourse.zolotyh.schedule.manager.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.zolotyh.schedule.exception.BackupLoadException;
import ru.yandex.javacourse.zolotyh.schedule.exception.ManagerSaveException;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;
import ru.yandex.javacourse.zolotyh.schedule.util.CSVTaskFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.javacourse.zolotyh.schedule.util.TaskUtil.getNewTask;
import static ru.yandex.javacourse.zolotyh.schedule.util.TaskUtil.getTestTasksAsStrings;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private Path backup;
    private final List<String> tasksAsStrings = getTestTasksAsStrings();

    @Override
    @BeforeEach
    public void beforeEach() {
        try {
            backup = Files.createTempFile(Paths.get("test_resources"), "backup_test", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        backup.toFile().deleteOnExit();
        taskManager = new FileBackedTaskManager(backup.toFile());
        super.beforeEach();
    }

    @Override
    @Test
    public void deleteAllTasksTest() {
        super.deleteAllTasksTest();
        final String[] lines = readBackup(backup);
        assertEquals(6, lines.length, "Количество строк в файле не совпадает.");
        assertEquals(CSVTaskFormat.getHeader(), lines[0], "Строка заголовоков не совпадает.");
        assertEquals(tasksAsStrings.get(3), lines[1], "Подзадачи 1 нет в файле.");
        assertEquals(tasksAsStrings.get(4), lines[2], "Подзадачи 2 нет в файле.");
        assertEquals(tasksAsStrings.get(5), lines[3], "Подзадачи 3 нет в файле.");
        assertEquals(tasksAsStrings.get(6), lines[4], "Эпика с тремя подзадачами нет в файле");
        assertEquals(tasksAsStrings.get(7), lines[5], "Эпика без подзадач нет в файле.");
    }

    @Override
    @Test
    public void deleteAllSubtasksTest() {
        super.deleteAllSubtasksTest();
        final String[] lines = readBackup(backup);
        assertEquals(6, lines.length, "Количество строк в файле не совпадает.");
        assertEquals(CSVTaskFormat.getHeader(), lines[0], "Строка заголовоков не совпадает.");
        assertEquals(tasksAsStrings.get(0), lines[1], "Задачи 1 нет в файле.");
        assertEquals(tasksAsStrings.get(1), lines[2], "Задачи 2 нет в файле.");
        assertEquals(tasksAsStrings.get(2), lines[3], "Задачи 3 нет в файле.");
        assertEquals("4,EPIC,Эпик с тремя подзадачами,NEW,Описание эпика с тремя подзадачами,null,null,",
                lines[4], "Эпика с тремя подзадачами нет в файле");
        assertEquals(tasksAsStrings.get(7), lines[5], "Эпика без подзадач нет в файле.");
    }

    @Override
    @Test
    public void deleteAllEpicsTest() {
        super.deleteAllEpicsTest();
        final String[] lines = readBackup(backup);
        assertEquals(4, lines.length, "Количество строк в файле не совпадает.");
        assertEquals(CSVTaskFormat.getHeader(), lines[0], "Строка заголовоков не совпадает.");
        assertEquals(tasksAsStrings.get(0), lines[1], "Задачи 1 нет в файле.");
        assertEquals(tasksAsStrings.get(1), lines[2], "Задачи 2 нет в файле.");
        assertEquals(tasksAsStrings.get(2), lines[3], "Задачи 3 нет в файле.");
    }

    @Override
    @Test
    public void addNewTaskTest() {
        super.addNewTaskTest();
        final String[] lines = readBackup(backup);
        assertEquals(10, lines.length, "Количество строк в файле не совпадает.");
        assertEquals(CSVTaskFormat.getHeader(), lines[0], "Строка заголовоков не совпадает.");
        assertEquals(tasksAsStrings.get(0), lines[1], "Задачи 1 нет в файле.");
        assertEquals(tasksAsStrings.get(1), lines[2], "Задачи 2 нет в файле.");
        assertEquals(tasksAsStrings.get(2), lines[3], "Задачи 3 нет в файле.");
        assertEquals("9,TASK,Новая задача,NEW,Описание задачи,45,2024-06-01T08:00,", lines[4],
                "Новой задачи нет в файле.");
        assertEquals(tasksAsStrings.get(3), lines[5], "Подзадачи 1 нет в файле.");
        assertEquals(tasksAsStrings.get(4), lines[6], "Подзадачи 2 нет в файле.");
        assertEquals(tasksAsStrings.get(5), lines[7], "Подзадачи 3 нет в файле.");
        assertEquals(tasksAsStrings.get(6), lines[8], "Эпика с тремя подзадачами нет в файле");
        assertEquals(tasksAsStrings.get(7), lines[9], "Эпика без подзадач нет в файле.");
    }

    @Override
    @Test
    public void addNewEpicTest() {
        super.addNewEpicTest();
        final String[] lines = readBackup(backup);
        assertEquals(10, lines.length, "Количество строк в файле не совпадает.");
        assertEquals(CSVTaskFormat.getHeader(), lines[0], "Строка заголовоков не совпадает.");
        assertEquals(tasksAsStrings.get(0), lines[1], "Задачи 1 нет в файле.");
        assertEquals(tasksAsStrings.get(1), lines[2], "Задачи 2 нет в файле.");
        assertEquals(tasksAsStrings.get(2), lines[3], "Задачи 3 нет в файле.");
        assertEquals(tasksAsStrings.get(3), lines[4], "Подзадачи 1 нет в файле.");
        assertEquals(tasksAsStrings.get(4), lines[5], "Подзадачи 2 нет в файле.");
        assertEquals(tasksAsStrings.get(5), lines[6], "Подзадачи 3 нет в файле.");
        assertEquals(tasksAsStrings.get(6), lines[7], "Эпика с тремя подзадачами нет в файле");
        assertEquals(tasksAsStrings.get(7), lines[8], "Эпика без подзадач нет в файле.");
        assertEquals("9,EPIC,Новый эпик,NEW,Описание нового эпика,null,null,", lines[9], "Эпика без подзадач нет в файле.");
    }

    @Override
    @Test
    public void addNewSubtaskTest() {
        super.addNewSubtaskTest();
        final String[] lines = readBackup(backup);
        assertEquals(10, lines.length, "Количество строк в файле не совпадает.");
        assertEquals(CSVTaskFormat.getHeader(), lines[0], "Строка заголовоков не совпадает.");
        assertEquals(tasksAsStrings.get(0), lines[1], "Задачи 1 нет в файле.");
        assertEquals(tasksAsStrings.get(1), lines[2], "Задачи 2 нет в файле.");
        assertEquals(tasksAsStrings.get(2), lines[3], "Задачи 3 нет в файле.");
        assertEquals(tasksAsStrings.get(3), lines[4], "Подзадачи 1 нет в файле.");
        assertEquals(tasksAsStrings.get(4), lines[5], "Подзадачи 2 нет в файле.");
        assertEquals(tasksAsStrings.get(5), lines[6], "Подзадачи 3 нет в файле.");
        assertEquals("9,SUBTASK,Новая подзадача,DONE,Описание подзадачи,15,2024-06-01T09:00,4,", lines[7],
                "Новой подзадачи нет в файле");
        assertEquals("4,EPIC,Эпик с тремя подзадачами,IN_PROGRESS,Описание эпика с тремя подзадачами,255,2024-05-01T10:00,",
                lines[8], "Эпика с тремя не поменял значения полей после добавления подзадачи.");
        assertEquals(tasksAsStrings.get(7), lines[9], "Эпика без подзадач нет в файле.");
    }

    @Override
    @Test
    public void updateTaskTest() {
        super.updateTaskTest();
        final String[] lines = readBackup(backup);
        assertEquals(9, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("1,TASK,Новая задача,NEW,Описание задачи,45,2024-06-01T08:00,", lines[1],
                "Сериализованная задача в файле не совпадает.");
    }

    @Override
    @Test
    public void updateEpicTest() {
        super.updateEpicTest();
        final String[] lines = readBackup(backup);
        assertEquals(9, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("4,EPIC,Новый эпик,IN_PROGRESS,Описание нового эпика,null,null,", lines[7],
                "Сериализованный эпик в файле не совпадает.");
    }

    @Override
    @Test
    public void updateSubtaskTest() {
        super.updateSubtaskTest();
        final String[] lines = readBackup(backup);
        assertEquals(9, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("6,SUBTASK,Новая подзадача,DONE,Описание подзадачи,15,2024-06-01T09:00,4,", lines[4],
                "Сериализованная подзадача в файле не совпадает.");
        assertEquals("4,EPIC,Эпик с тремя подзадачами,DONE,Описание эпика с тремя подзадачами,165,2024-05-01T10:00,",
                lines[7], "Сериализованный эпик в файле не совпадает.");
    }

    @Override
    @Test
    public void deleteTaskTest() {
        super.deleteTaskTest();
        final String[] lines = readBackup(backup);
        assertEquals(8, lines.length, "Количество строк в файле не совпадает.");
    }

    @Override
    @Test
    public void deleteEpicTest() {
        super.deleteEpicTest();
        final String[] lines = readBackup(backup);
        assertEquals(5, lines.length, "Количество строк в файле не совпадает.");
    }

    @Override
    @Test
    public void deleteSubtaskTest() {
        super.deleteSubtaskTest();
        // Проверяем, что подзадача успешно удалена из файла, а строка заголовков и эпик остались
        final String[] lines = readBackup(backup);
        assertEquals(8, lines.length, "Количество строк в файле не совпадает.");
    }

    @Test
    public void shouldThrowsManagerSaveException() {
        // Создаем менеджер задач и передаем ему файл по несуществующему пути
        final File wrong = new File("nonexistent_path/backup.csv");
        taskManager = new FileBackedTaskManager(wrong);
        final Task task = getNewTask();
        assertThrows(ManagerSaveException.class, () -> taskManager.addNewTask(task));
    }

    @Test
    public void shouldLoadAllTasksFromFile() {
        final String serializedTasks = CSVTaskFormat.getHeader() + System.lineSeparator() +
                tasksAsStrings.get(0) + System.lineSeparator() +
                tasksAsStrings.get(1) + System.lineSeparator() +
                tasksAsStrings.get(2) + System.lineSeparator() +
                tasksAsStrings.get(3) + System.lineSeparator() +
                tasksAsStrings.get(4) + System.lineSeparator() +
                tasksAsStrings.get(5) + System.lineSeparator() +
                tasksAsStrings.get(6) + System.lineSeparator() +
                tasksAsStrings.get(7);
        try (FileWriter writer = new FileWriter(backup.toFile(), StandardCharsets.UTF_8)) {
            writer.write(serializedTasks);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить задачи в файл", e);
        }

        final FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(backup.toFile());
        newManager.getAllTasks().forEach(System.out::println);
        assertEquals(3, newManager.getAllTasks().size(), "Количество десериализованных задач не соответствует.");
        assertEquals(2, newManager.getAllEpics().size(), "Количество десериализованных эпиков не соответствует.");
        assertEquals(3, newManager.getAllSubtasks().size(), "Количество десериализованных подзадач не соответствует.");
    }

    @Test
    public void shouldLoadCorrectFromEmptyFile() {
        // Записываем в файл строку заголовка
        final String heading = "id,type,name,status,description,epic";
        try (FileWriter writer = new FileWriter(backup.toFile(), StandardCharsets.UTF_8)) {
            writer.write(heading);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить строку заголовка в файл", e);
        }

        final FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(backup.toFile());
        assertEquals(0, newManager.getAllTasks().size(), "Список задач не пустой.");
        assertEquals(0, newManager.getAllEpics().size(), "Список эпиков не пустой.");
        assertEquals(0, newManager.getAllSubtasks().size(), "Список подзадач не пустой.");
    }

    private String[] readBackup(Path backup) {
        final String content;
        try {
            content = Files.readString(backup);
        } catch (IOException e) {
            throw new BackupLoadException("Не удалось прочитать файл бэкапа", e);
        }
        return content.split(System.lineSeparator());
    }
}
