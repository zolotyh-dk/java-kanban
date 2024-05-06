package ru.yandex.javacourse.zolotyh.schedule.manager.task_manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
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
//        backup.toFile().deleteOnExit();
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
        // Проверяем, что задача успешно обновлена в файле
        final String[] lines = readBackup(backup);
        assertEquals(2, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("1,TASK,Новая задача,IN_PROGRESS,Описание новой задачи,", lines[1],
                "Сериализованная задача в файле не совпадает.");
    }

    @Override
    @Test
    public void updateEpicTest() {
        super.updateEpicTest();
        // Проверяем, что эпик успешно обновлен в файле
        final String[] lines = readBackup(backup);
        assertEquals(2, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("1,EPIC,Новый эпик,NEW,Описание нового эпика,", lines[1],
                "Сериализованный эпик в файле не совпадает.");
    }

    @Override
    @Test
    public void updateSubtaskTest() {
        super.updateSubtaskTest();
        // Проверяем, что подзадача успешно обновлена в файле и статус эпика изменен
        final String[] lines = readBackup(backup);
        assertEquals(3, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("2,SUBTASK,Обновленная подзадача,IN_PROGRESS,Описание обновленной подзадачи,1,", lines[1],
                "Сериализованная подзадача в файле не совпадает.");
        assertEquals("1,EPIC,Новый эпик,IN_PROGRESS,Описание нового эпика,", lines[2],
                "Сериализованный эпик в файле не совпадает.");
    }

    //Тесты на удаление объектов ⬇️
    @Override
    @Test
    public void deleteTaskTest() {
        super.deleteTaskTest();
        // Проверяем, что задача успешно удалена из файла и в файле только строка заголовков
        final String[] lines = readBackup(backup);
        assertEquals(1, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("id,type,name,status,description,epic", lines[0],
                "Строка заголовоков не совпадает.");
    }

    @Override
    @Test
    public void deleteEpicTest() {
        super.deleteEpicTest();
        // Проверяем, что эпик успешно удален из файла и в файле только строка заголовков
        final String[] lines = readBackup(backup);
        assertEquals(1, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("id,type,name,status,description,epic", lines[0],
                "Строка заголовоков не совпадает.");
    }

    @Override
    @Test
    public void deleteSubtaskTest() {
        super.deleteSubtaskTest();
        // Проверяем, что подзадача успешно удалена из файла, а строка заголовков и эпик остались
        final String[] lines = readBackup(backup);
        assertEquals(2, lines.length, "Количество строк в файле не совпадает.");
        assertEquals("id,type,name,status,description,epic", lines[0],
                "Строка заголовоков не совпадает.");
        assertEquals("1,EPIC,Новый эпик,NEW,Описание эпика,", lines[1],
                "Сериализованый эпик 1 в файле не совпадает.");
    }


    @Test
    public void shouldThrowsManagerSaveException() {
        // Создаем менеджер задач и передаем ему файл по несуществующему пути
        final File wrong = new File("nonexistent_path/backup.csv");
        taskManager = new FileBackedTaskManager(wrong);
        final Task task = new Task(null, "Новая задача", "Описание задачи", Status.NEW);
        assertThrows(ManagerSaveException.class, () -> taskManager.addNewTask(task));
    }

    @Test
    public void shouldLoadTaskFromFile() {
        final String serializedTask = "id,type,name,status,description,epic" + System.lineSeparator() +
                "1,TASK,Новая задача,NEW,Описание задачи";
        try (FileWriter writer = new FileWriter(backup.toFile(), StandardCharsets.UTF_8)) {
            writer.write(serializedTask);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить задачи в файл", e);
        }

        final FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(backup.toFile());
        assertEquals(1, newManager.getTaskById(1).getId());
        assertEquals("Новая задача", newManager.getTaskById(1).getName());
        assertEquals("Описание задачи", newManager.getTaskById(1).getDescription());
        assertEquals(Status.NEW, newManager.getTaskById(1).getStatus());
    }

    @Test
    public void shouldLoadEpicWithSubtaskFromFile() {
        final String serializedTask = "id,type,name,status,description,epic" + System.lineSeparator() +
                                      "1,EPIC,Эпик с подзадачей,IN_PROGRESS,Описание эпика," + System.lineSeparator() +
                                      "2,SUBTASK,Подзадача,IN_PROGRESS,Описание подзадачи,1";
        try (FileWriter writer = new FileWriter(backup.toFile(), StandardCharsets.UTF_8)) {
            writer.write(serializedTask);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить эпик и его подзадачу в файл", e);
        }

        final FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(backup.toFile());
        assertEquals(1, newManager.getEpicById(1).getId());
        assertEquals("Эпик с подзадачей", newManager.getEpicById(1).getName());
        assertEquals("Описание эпика", newManager.getEpicById(1).getDescription());
        assertEquals(Status.IN_PROGRESS, newManager.getEpicById(1).getStatus());
        assertEquals(1, newManager.getEpicById(1).getSubtaskIds().size());
        assertEquals(2, newManager.getSubtaskById(2).getId());
        assertEquals("Подзадача", newManager.getSubtaskById(2).getName());
        assertEquals("Описание подзадачи", newManager.getSubtaskById(2).getDescription());
        assertEquals(Status.IN_PROGRESS, newManager.getSubtaskById(2).getStatus());
        assertEquals(1, newManager.getSubtaskById(2).getEpicId());
    }

    @Test
    public void shouldLoadAllTasksFromFile() {
        final String serializedTasks = "id,type,name,status,description,epic" + System.lineSeparator() +
                                       "1,TASK,Задача 1,NEW,Описание задачи 1," + System.lineSeparator() +
                                       "2,TASK,Задача 2,IN_PROGRESS,Описание задачи 2," + System.lineSeparator() +
                                       "3,EPIC,Эпик с тремя подзадачами,IN_PROGRESS,Описание эпика с тремя подзадачами," + System.lineSeparator() +
                                       "4,SUBTASK,Подзадача 1,NEW,Описание подзадачи 1,3," + System.lineSeparator() +
                                       "5,SUBTASK,Подзадача 2,IN_PROGRESS,Описание подзадачи 2,3," + System.lineSeparator() +
                                       "6,SUBTASK,Подзадача 3,DONE,Описание подзадачи 3,3," + System.lineSeparator() +
                                       "7,EPIC,Эпик без подзадач,NEW,Описание эпика без подзадач";
        try (FileWriter writer = new FileWriter(backup.toFile(), StandardCharsets.UTF_8)) {
            writer.write(serializedTasks);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить задачи в файл", e);
        }

        // Проверяем, что количество десереиализованных задач всех типов соответствует ожидаемому
        final FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(backup.toFile());
        assertEquals(2, newManager.getAllTasks().size(), "Количество десериализованных задач не соответствует.");
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

        // Проверяем, что списки задач пусты
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
