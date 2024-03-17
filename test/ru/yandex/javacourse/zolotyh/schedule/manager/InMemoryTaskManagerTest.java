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

class InMemoryTaskManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void addNewTask() {
        Task task = new Task(null, "Test addNewTask", "Test addNewTask description", Status.NEW);
        final int id = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTaskById(id);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    public void addNewEpicWithSubtask() {
        Epic epic = new Epic(null, "Test addNewEpicWithSubtask epic",
                "Test addNewEpicSubtask epic description");
        final int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(null, "Test addNewSubtask", "Test addNewSubtask description",
                Status.DONE, epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(savedEpic.getStatus(), Status.DONE, "Эпик не меняет статус при добавлении подзадачи.");

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    public void updateTask() {
        Task old = new Task(null, "Старая задача из теста updateTask", "Описание старой задачи",
                Status.NEW);
        final int id = taskManager.addNewTask(old);
        Task updated = new Task(id, "Новая задача из теста updateTask", "Описание новой задачи",
                Status.IN_PROGRESS);
        taskManager.updateTask(updated);
        assertEquals(updated, taskManager.getTaskById(id), "Задача не обновляется.");
    }

    @Test
    public void updateEpic() {
        Epic old = new Epic(null, "Старый эпик из теста updateEpic", "Описание старого эпика");
        final int id = taskManager.addNewEpic(old);
        Epic updated = new Epic(id, "Новый эпик из теста updateEpic", "Описание нового эпика");
        taskManager.updateEpic(updated);
        assertEquals(updated, taskManager.getEpicById(id), "Эпик не обновляется.");
    }
}