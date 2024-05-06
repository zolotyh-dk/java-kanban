package ru.yandex.javacourse.zolotyh.schedule.manager.task_manager;

import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {
    private Path backup;

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

//    //Тесты на добавление новых объектов ⬇️
//    @Override
//    @Test
//    public void addNewTask() {
//        super.addNewTask();
//        // Проверяем, что количество строк в файле и строка задачи соответствуют ожидаемым.
//        final String[] lines = readBackup(backup);
//        assertEquals(2, lines.length, "Количество строк в файле не совпадает.");
//        assertEquals("1,TASK,Новая задача,NEW,Описание задачи,", lines[1],
//                "Сериализованная задача в файле не совпадает.");
//    }
//
//    @Override
//    @Test
//    public void addNewEpicWithSubtask() {
//        super.addNewEpicWithSubtask();
//        // Проверяем, что количество строк в файле и строка задачи соответствуют ожидаемым.
//        final String[] lines = readBackup(backup);
//        assertEquals(3, lines.length, "Количество строк в файле не совпадает.");
//        assertEquals("2,SUBTASK,Новая подзадача,DONE,Описание подзадачи,1,", lines[1],
//                "Сериализованая подзадача в файле не совпадает.");
//        assertEquals("1,EPIC,Новый эпик,DONE,Описание эпика,", lines[2],
//                "Сериализованый эпик в файле не совпадает.");
//    }
//
//    //Тесты на обновление существующих объектов ⬇️
//    @Override
//    @Test
//    public void updateTask() {
//        super.updateTask();
//        // Проверяем, что задача успешно обновлена в файле
//        final String[] lines = readBackup(backup);
//        assertEquals(2, lines.length, "Количество строк в файле не совпадает.");
//        assertEquals("1,TASK,Новая задача,IN_PROGRESS,Описание новой задачи,", lines[1],
//                "Сериализованная задача в файле не совпадает.");
//    }
//
//    @Override
//    @Test
//    public void updateEpic() {
//        super.updateEpic();
//        // Проверяем, что эпик успешно обновлен в файле
//        final String[] lines = readBackup(backup);
//        assertEquals(2, lines.length, "Количество строк в файле не совпадает.");
//        assertEquals("1,EPIC,Новый эпик,NEW,Описание нового эпика,", lines[1],
//                "Сериализованный эпик в файле не совпадает.");
//    }
//
//    @Override
//    @Test
//    public void updateSubtask() {
//        super.updateSubtask();
//        // Проверяем, что подзадача успешно обновлена в файле и статус эпика изменен
//        final String[] lines = readBackup(backup);
//        assertEquals(3, lines.length, "Количество строк в файле не совпадает.");
//        assertEquals("2,SUBTASK,Обновленная подзадача,IN_PROGRESS,Описание обновленной подзадачи,1,", lines[1],
//                "Сериализованная подзадача в файле не совпадает.");
//        assertEquals("1,EPIC,Новый эпик,IN_PROGRESS,Описание нового эпика,", lines[2],
//                "Сериализованный эпик в файле не совпадает.");
//    }
//
//    //Тесты на удаление объектов ⬇️
//    @Override
//    @Test
//    public void deleteTask() {
//        super.deleteTask();
//        // Проверяем, что задача успешно удалена из файла и в файле только строка заголовков
//        final String[] lines = readBackup(backup);
//        assertEquals(1, lines.length, "Количество строк в файле не совпадает.");
//        assertEquals("id,type,name,status,description,epic", lines[0],
//                "Строка заголовоков не совпадает.");
//    }
//
//    @Override
//    @Test
//    public void deleteEpic() {
//        super.deleteEpic();
//        // Проверяем, что эпик успешно удален из файла и в файле только строка заголовков
//        final String[] lines = readBackup(backup);
//        assertEquals(1, lines.length, "Количество строк в файле не совпадает.");
//        assertEquals("id,type,name,status,description,epic", lines[0],
//                "Строка заголовоков не совпадает.");
//    }
//
//    @Override
//    @Test
//    public void deleteSubtask() {
//        super.deleteSubtask();
//        // Проверяем, что подзадача успешно удалена из файла, а строка заголовков и эпик остались
//        final String[] lines = readBackup(backup);
//        assertEquals(2, lines.length, "Количество строк в файле не совпадает.");
//        assertEquals("id,type,name,status,description,epic", lines[0],
//                "Строка заголовоков не совпадает.");
//        assertEquals("1,EPIC,Новый эпик,NEW,Описание эпика,", lines[1],
//                "Сериализованый эпик 1 в файле не совпадает.");
//    }
//
//    @Override
//    @Test
//    public void deleteAllTasks() {
//        super.deleteAllTasks();
//        // Проверяем, что задача успешно удалена из файла и в файле только строка заголовков
//        final String[] lines = readBackup(backup);
//        assertEquals(1, lines.length, "Количество строк в файле не совпадает.");
//        assertEquals("id,type,name,status,description,epic", lines[0],
//                "Строка заголовоков не совпадает.");
//    }
//
//    @Override
//    @Test
//    public void deleteAllSubtasks() {
//        super.deleteAllSubtasks();
//        // Проверяем, что подзадачи успешно удалена из файла, а 2 эпика остались
//        final String[] lines = readBackup(backup);
//        assertEquals(3, lines.length, "Количество строк в файле не совпадает.");
//        assertEquals("1,EPIC,Эпик 1,NEW,Описание эпика 1,", lines[1],
//                "Сериализованый эпик 1 в файле не совпадает.");
//        assertEquals("2,EPIC,Эпик 2,NEW,Описание эпика 2,", lines[2],
//                "Сериализованый эпик 2 в файле не совпадает.");
//    }
//
//    @Override
//    @Test
//    public void deleteAllEpics() {
//        super.deleteAllEpics();
//        // Проверяем, что подзадачи и эпики успешно удалены из файла, а в файле осталась строка заголовков
//        final String[] lines = readBackup(backup);
//        assertEquals(1, lines.length, "Количество строк в файле не совпадает.");
//        assertEquals("id,type,name,status,description,epic", lines[0],
//                "Строка заголовоков не совпадает.");
//    }
//
//    //Тесты на сохранение задач в файл по несуществующему пути ⬇️
//    @Test
//    public void shouldThrowsManagerSaveException() {
//        // Создаем менеджер задач и передаем ему файл по несуществующему пути
//        final File wrong = new File("nonexistent_path/backup.csv");
//        taskManager = new FileBackedTaskManager(wrong);
//
//        // Создаем новую задачу
//        final Task task = new Task(null, "Новая задача", "Описание задачи", Status.NEW);
//
//        // Проверяем что вызов метода сохранения задачи выбрасывает ManagerSaveException
//        assertThrows(ManagerSaveException.class, () -> taskManager.addNewTask(task));
//    }
//
//    //Тесты на восстановление менеджера из файла ⬇️
//    @Test
//    public void shouldLoadTaskFromFile() {
//        // Записываем в файл задачу
//        final String serializedTask = "id,type,name,status,description,epic" + System.lineSeparator() +
//                "1,TASK,Новая задача,NEW,Описание задачи";
//        try (FileWriter writer = new FileWriter(backup.toFile(), StandardCharsets.UTF_8)) {
//            writer.write(serializedTask);
//        } catch (IOException e) {
//            throw new ManagerSaveException("Не удалось сохранить задачи в файл", e);
//        }
//
//        // Проверяем, что поля десериализованной задачи соответствуют ожидаемым
//        final FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(backup.toFile());
//        assertEquals(1, newManager.getTaskById(1).getId());
//        assertEquals("Новая задача", newManager.getTaskById(1).getName());
//        assertEquals("Описание задачи", newManager.getTaskById(1).getDescription());
//        assertEquals(Status.NEW, newManager.getTaskById(1).getStatus());
//    }
//
//    @Test
//    public void shouldLoadEpicWithSubtaskFromFile() {
//        // Записываем в файл эпик и его подзадачу
//        final String serializedTask = "id,type,name,status,description,epic" + System.lineSeparator() +
//                                      "1,EPIC,Эпик с подзадачей,IN_PROGRESS,Описание эпика," + System.lineSeparator() +
//                                      "2,SUBTASK,Подзадача,IN_PROGRESS,Описание подзадачи,1";
//        try (FileWriter writer = new FileWriter(backup.toFile(), StandardCharsets.UTF_8)) {
//            writer.write(serializedTask);
//        } catch (IOException e) {
//            throw new ManagerSaveException("Не удалось сохранить эпик и его подзадачу в файл", e);
//        }
//
//        // Проверяем, что поля десериализованных эпика и подзадачи соответствуют ожидаемым
//        final FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(backup.toFile());
//        assertEquals(1, newManager.getEpicById(1).getId());
//        assertEquals("Эпик с подзадачей", newManager.getEpicById(1).getName());
//        assertEquals("Описание эпика", newManager.getEpicById(1).getDescription());
//        assertEquals(Status.IN_PROGRESS, newManager.getEpicById(1).getStatus());
//        assertEquals(1, newManager.getEpicById(1).getSubtaskIds().size());
//        assertEquals(2, newManager.getSubtaskById(2).getId());
//        assertEquals("Подзадача", newManager.getSubtaskById(2).getName());
//        assertEquals("Описание подзадачи", newManager.getSubtaskById(2).getDescription());
//        assertEquals(Status.IN_PROGRESS, newManager.getSubtaskById(2).getStatus());
//        assertEquals(1, newManager.getSubtaskById(2).getEpicId());
//    }
//
//    @Test
//    public void shouldLoadAllTasksFromFile() {
//        // Записываем в файл несколько задач разных типов
//        final String serializedTasks = "id,type,name,status,description,epic" + System.lineSeparator() +
//                                       "1,TASK,Задача 1,NEW,Описание задачи 1," + System.lineSeparator() +
//                                       "2,TASK,Задача 2,IN_PROGRESS,Описание задачи 2," + System.lineSeparator() +
//                                       "3,EPIC,Эпик с тремя подзадачами,IN_PROGRESS,Описание эпика с тремя подзадачами," + System.lineSeparator() +
//                                       "4,SUBTASK,Подзадача 1,NEW,Описание подзадачи 1,3," + System.lineSeparator() +
//                                       "5,SUBTASK,Подзадача 2,IN_PROGRESS,Описание подзадачи 2,3," + System.lineSeparator() +
//                                       "6,SUBTASK,Подзадача 3,DONE,Описание подзадачи 3,3," + System.lineSeparator() +
//                                       "7,EPIC,Эпик без подзадач,NEW,Описание эпика без подзадач";
//        try (FileWriter writer = new FileWriter(backup.toFile(), StandardCharsets.UTF_8)) {
//            writer.write(serializedTasks);
//        } catch (IOException e) {
//            throw new ManagerSaveException("Не удалось сохранить задачи в файл", e);
//        }
//
//        // Проверяем, что количество десереиализованных задач всех типов соответствует ожидаемому
//        final FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(backup.toFile());
//        assertEquals(2, newManager.getAllTasks().size(), "Количество десериализованных задач не соответствует.");
//        assertEquals(2, newManager.getAllEpics().size(), "Количество десериализованных эпиков не соответствует.");
//        assertEquals(3, newManager.getAllSubtasks().size(), "Количество десериализованных подзадач не соответствует.");
//    }
//
//    @Test
//    public void shouldLoadCorrectFromEmptyFile() {
//        // Записываем в файл строку заголовка
//        final String heading = "id,type,name,status,description,epic";
//        try (FileWriter writer = new FileWriter(backup.toFile(), StandardCharsets.UTF_8)) {
//            writer.write(heading);
//        } catch (IOException e) {
//            throw new ManagerSaveException("Не удалось сохранить строку заголовка в файл", e);
//        }
//
//        // Проверяем, что списки задач пусты
//        final FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(backup.toFile());
//        assertEquals(0, newManager.getAllTasks().size(), "Список задач не пустой.");
//        assertEquals(0, newManager.getAllEpics().size(), "Список эпиков не пустой.");
//        assertEquals(0, newManager.getAllSubtasks().size(), "Список подзадач не пустой.");
//    }
//
//    private String[] readBackup(Path backup) {
//        final String content;
//        try {
//            content = Files.readString(backup);
//        } catch (IOException e) {
//            throw new BackupLoadException("Не удалось прочитать файл бэкапа", e);
//        }
//        return content.split(System.lineSeparator());
//    }
}
