package ru.yandex.javacourse.zolotyh.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;
import ru.yandex.javacourse.zolotyh.schedule.util.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static HistoryManager historyManager;
    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = taskManager.getHistoryManager();
    }

    @Test
    public void shouldAddTasksToHistoryWhenGetById() {
        final Task task = new Task(null, "Новая задача", "Описание задачи", Status.NEW);
        final int taskId = taskManager.addNewTask(task);

        final Epic epic = new Epic(null, "Новый эпик", "Описание эпика");
        final int epicId = taskManager.addNewEpic(epic);

        final Subtask subtask = new Subtask(null, "Новая подзадача", "Описание подзадачи",
                Status.DONE, epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        taskManager.getTaskById(taskId);
        taskManager.getEpicById(epicId);
        taskManager.getSubtaskById(subtaskId);

        final List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "Размер истории не совпадает.");
        assertEquals(task, history.get(0), "Задача не попала в историю.");
        assertEquals(epic, history.get(1), "Эпик не попал в историю.");
        assertEquals(subtask, history.get(2), "Подзадача не попала в историю.");
    }

    @Test
    public void shouldRemoveFirstTaskInHistoryWhenSizeMoreThen10() {
        final Task first = new Task(null, "Первая задача в истории", "Описание задачи", Status.NEW);
        final int firstId = taskManager.addNewTask(first);
        taskManager.getTaskById(firstId);

        for (int i = 0; i < 9; i++) {
            Task task = new Task(null, "Очередная задча в истории", "Описание задчи", Status.NEW);
            int id = taskManager.addNewTask(task);
            taskManager.getTaskById(id);
        }

        final Task eleventh = new Task(null, "Одиннадцатая задача в истории", "Описание задачи", Status.NEW);
        final int eleventhId = taskManager.addNewTask(eleventh);
        taskManager.getTaskById(eleventhId);
        final List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size(), "Длина истории не совпадает.");
        assertFalse(history.contains(first));
        assertEquals(eleventh, history.get(9));
    }

    @Test
    public void shouldContainOriginalTaskAfterUpdate() {
        final Task original = new Task(null, "Исходная задача", "Описание задачи", Status.NEW);
        final int originalId = taskManager.addNewTask(original);
        taskManager.getTaskById(originalId);

        final Task updated = new Task(originalId, "Обновленная задача", "Описание задачи", Status.IN_PROGRESS);
        taskManager.updateTask(updated);

        final List<Task> history = historyManager.getHistory();

        assertEquals(original, history.get(0), "Исходная задача не сохранилась в истории после обновления.");
        assertEquals(original.getName(), history.get(0).getName(), "Имя исходной задачи не сохранилось в истории");
        assertEquals(original.getDescription(), history.get(0).getDescription(),
                "Описание исходной задачи не сохранилось в истории");
        assertEquals(original.getStatus(), history.get(0).getStatus(), "Статус исходной задачи не сохранилось в истории");
    }

    @Test
    public void shouldContainOriginalEpicAfterUpdate() {
        final Epic original = new Epic(null, "Исходный эпик", "Описание эпика");
        final int originalId = taskManager.addNewEpic(original);
        taskManager.getEpicById(originalId);

        final Epic updated = new Epic(originalId, "Обновленный эпик", "Описание эпика");
        taskManager.updateEpic(updated);

        final List<Task> history = historyManager.getHistory();

        assertEquals(original, history.get(0), "Исходный эпик не сохранился в истории после обновления.");
        assertEquals(original.getName(), history.get(0).getName(), "Имя исходного эпика не сохранилось в истории");
        assertEquals(original.getDescription(), history.get(0).getDescription(),
                "Описание исходного эпика не сохранилось в истории");
        assertEquals(original.getStatus(), history.get(0).getStatus(), "Статус исходного эпика не сохранился в истории");
    }
}