package ru.yandex.javacourse.zolotyh.schedule.manager.history_manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.manager.task_manager.TaskManager;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;
import ru.yandex.javacourse.zolotyh.schedule.util.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    //Тесты на добавление задач в историю ⬇️
    @Test
    public void shouldAddTasksToHistoryWhenGetById() {
        // Создаем задачу, эпик и подзадачу
        final Task task = new Task(null, "Новая задача", "Описание задачи", Status.NEW);
        final int taskId = taskManager.addNewTask(task);
        final Epic epic = new Epic(null, "Новый эпик", "Описание эпика");
        final int epicId = taskManager.addNewEpic(epic);
        final Subtask subtask = new Subtask(null, "Новая подзадача", "Описание подзадачи",
                Status.DONE, epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        // Добавляем задачу, эпик и подзадачу в историю
        taskManager.getTaskById(taskId);
        taskManager.getEpicById(epicId);
        taskManager.getSubtaskById(subtaskId);

        // Получаем историю и проверяем, что все объекты добавлены в историю
        final List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size(), "Размер истории не совпадает.");
        assertEquals(task, history.get(0), "Задача не попала в историю.");
        assertEquals(epic, history.get(1), "Эпик не попал в историю.");
        assertEquals(subtask, history.get(2), "Подзадача не попала в историю.");
    }

    @Test
    public void shouldNotContainDuplicates() {
        // Создаем задачу, эпик и подзадачу
        final Task task = new Task(null, "Новая задача", "Описание задачи", Status.NEW);
        final int taskId = taskManager.addNewTask(task);
        final Epic epic = new Epic(null, "Новый эпик", "Описание эпика");
        final int epicId = taskManager.addNewEpic(epic);
        final Subtask subtask = new Subtask(null, "Новая подзадача", "Описание подзадачи",
                Status.DONE, epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        // Добавляем задачу, эпик и подзадачу в историю
        taskManager.getTaskById(taskId);
        taskManager.getEpicById(epicId);
        taskManager.getSubtaskById(subtaskId);

        // Добавляем объекты второй раз
        taskManager.getTaskById(taskId);
        taskManager.getEpicById(epicId);
        taskManager.getSubtaskById(subtaskId);

        // Проверяем, что в истории нет дубликатов
        final List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size(), "Размер истории не совпадает.");
    }

    //Тесты на обновление задач в истории ⬇️
    @Test
    public void shouldContainOriginalTaskAfterUpdate() {
        // Создаем исходную задачу и обновленную задачу
        final Task original = new Task(null, "Исходная задача", "Описание задачи", Status.NEW);
        final int originalId = taskManager.addNewTask(original);
        taskManager.getTaskById(originalId);
        final Task updated = new Task(originalId, "Обновленная задача", "Описание задачи", Status.IN_PROGRESS);

        // Обновляем задачу
        taskManager.updateTask(updated);

        // Получаем историю и проверяем, что исходная задача сохранилась в истории
        final List<Task> history = taskManager.getHistory();
        assertEquals(original, history.get(0), "Исходная задача не сохранилась в истории после обновления.");
        assertEquals(original.getName(), history.get(0).getName(), "Имя исходной задачи не сохранилось в истории");
        assertEquals(original.getDescription(), history.get(0).getDescription(),
                "Описание исходной задачи не сохранилось в истории");
        assertEquals(original.getStatus(), history.get(0).getStatus(), "Статус исходной задачи не сохранился в истории");
    }

    @Test
    public void shouldContainOriginalEpicAfterUpdate() {
        // Создаем исходный эпик и обновленный эпик
        final Epic original = new Epic(null, "Исходный эпик", "Описание эпика");
        final int originalId = taskManager.addNewEpic(original);
        taskManager.getEpicById(originalId);
        final Epic updated = new Epic(originalId, "Обновленный эпик", "Описание эпика");

        // Обновляем эпик
        taskManager.updateEpic(updated);

        // Получаем историю и проверяем, что исходный эпик сохранился в истории
        final List<Task> history = taskManager.getHistory();
        assertEquals(original, history.get(0), "Исходный эпик не сохранился в истории после обновления.");
        assertEquals(original.getName(), history.get(0).getName(), "Имя исходного эпика не сохранилось в истории");
        assertEquals(original.getDescription(), history.get(0).getDescription(),
                "Описание исходного эпика не сохранилось в истории");
        assertEquals(original.getStatus(), history.get(0).getStatus(), "Статус исходного эпика не сохранился в истории");
    }

    @Test
    public void shouldContainOriginalSubtaskAfterUpdate() {
        // Создаем исходную эпик, подзадачу для него и обновленную подзадачу для того же эпика
        final Epic epic = new Epic(null, "Эпик", "Описание эпика");
        final int epicId = taskManager.addNewEpic(epic);
        final Subtask original = new Subtask(null, "Исходная подзадача",
                "Описание исходной подзадачи", Status.DONE, epicId);
        final int originalId = taskManager.addNewSubtask(original);
        taskManager.getSubtaskById(originalId);
        final Subtask updated = new Subtask(originalId, "Обновленная подзадача",
                "Описание обновленной подзадачи", Status.IN_PROGRESS, epicId);

        // Обновляем подзадачу
        taskManager.updateSubtask(updated);

        // Получаем историю и проверяем, что исходная подзадача сохранилась в истории
        final List<Task> history = taskManager.getHistory();
        assertEquals(original, history.get(0), "Исходная подзадача не сохранилсась в истории после обновления.");
        assertEquals(original.getName(), history.get(0).getName(), "Имя исходной подзадачи не сохранилось в истории");
        assertEquals(original.getDescription(), history.get(0).getDescription(),
                "Описание исходной подзадачи не сохранилось в истории");
        assertEquals(original.getStatus(), history.get(0).getStatus(),
                "Статус исходной подзадачи не сохранился в истории");
    }

    //Тесты на удаление задач из истории ⬇️
    @Test
    public void shouldRemoveTaskFromHistoryWhenTaskRemovedFromRepository() {
        // Создаем задачу, эпик и подзадачу
        final Task task = new Task(null, "Новая задача", "Описание задачи", Status.NEW);
        final int taskId = taskManager.addNewTask(task);
        final Epic epic = new Epic(null, "Новый эпик", "Описание эпика");
        final int epicId = taskManager.addNewEpic(epic);
        final Subtask subtask = new Subtask(null, "Новая подзадача", "Описание подзадачи",
                Status.DONE, epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        // Добавляем задачу, эпик и подзадачу в историю
        taskManager.getTaskById(taskId);
        taskManager.getEpicById(epicId);
        taskManager.getSubtaskById(subtaskId);

        // Удаляем задачу, получаем историю и проверяем, что задача удалена из истории
        taskManager.deleteTask(taskId);
        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "Размер истории не совпадает.");
        assertEquals(epic, history.get(0), "Эпик не сместился к началу истории просмотров.");
        assertEquals(subtask, history.get(1), "Подзадача не сместилась к началу истории просмотров");

        // Удаляем подзадачу, получаем историю и проверяем, что подзадача удалена из истории
        taskManager.deleteSubtask(subtaskId);
        history = taskManager.getHistory();
        assertEquals(1, history.size(), "Размер истории не совпадает.");
        assertEquals(epic, history.get(0), "Эпик отсутствует в истории.");

        // Удаляем эпик, получаем историю и проверяем, что история пуста
        taskManager.deleteEpic(epicId);
        history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой.");
    }

    @Test
    public void shouldDeleteTasksFromHistoryWhenDeleteAllFromRepository() {
        // Создание задач
        final Task task1 = new Task(null, "Новая задача 1", "Описание задачи 1", Status.NEW);
        final int task1Id = taskManager.addNewTask(task1);
        final Task task2 = new Task(null, "Новая задача 2", "Описание задачи 2", Status.NEW);
        final int task2Id = taskManager.addNewTask(task2);

        // Создание эпиков
        final Epic epic1 = new Epic(null, "Новый эпик 1", "Описание эпика 1");
        final int epic1Id = taskManager.addNewEpic(epic1);
        final Epic epic2 = new Epic(null, "Новый эпик 2", "Описание эпика 2");
        final int epic2Id = taskManager.addNewEpic(epic2);

        // Создание подзадач
        final Subtask subtask1 = new Subtask(null, "Новая подзадача 1", "Описание подзадачи 1",
                Status.DONE, epic1Id);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);
        final Subtask subtask2 = new Subtask(null, "Новая подзадача 2", "Описание подзадачи 2",
                Status.DONE, epic2Id);
        final int subtask2Id = taskManager.addNewSubtask(subtask2);

        // Добавление задач, эпиков и подзадач в историю
        taskManager.getTaskById(task1Id);
        taskManager.getTaskById(task2Id);
        taskManager.getEpicById(epic1Id);
        taskManager.getEpicById(epic2Id);
        taskManager.getSubtaskById(subtask1Id);
        taskManager.getSubtaskById(subtask2Id);

        // Удаление всех задач, эпиков и подзадач
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();

        // Проверка, что история теперь пуста
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой.");
    }

    //Тесты для работы с пустой историей ⬇️
    @Test
    public void historyShouldBeEmptyWhenNoTasksAdded() {
        // Получаем историю и проверяем, что история пуста
        final List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой.");
    }

    @Test
    public void removeNotExistedTaskFromHistory() {
        // Создаем новую задачу, эпик и подзадачу
        final Task task = new Task(null, "Новая задача", "Описание задачи", Status.NEW);
        final int taskId = taskManager.addNewTask(task);
        final Epic epic = new Epic(null, "Новый эпик", "Описание эпика");
        final int epicId = taskManager.addNewEpic(epic);
        final Subtask subtask = new Subtask(null, "Новая подзадача", "Описание подзадачи",
                Status.DONE, epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        // Удаляем задачу из репозитория, проверяем что история пуста
        taskManager.deleteTask(taskId);
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой.");

        // Удаляем подзадачу из репозитория, проверяем что история пуста
        taskManager.deleteSubtask(subtaskId);
        history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой.");

        // Удаляем эпик из репозитория, проверяем что история пуста
        taskManager.deleteEpic(epicId);
        history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой.");
    }
}