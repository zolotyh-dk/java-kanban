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
        final Task task = new Task(null, "Новая задача из теста addNewTask", "Описание задачи", Status.NEW);
        final int id = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTaskById(id);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(task.getName(), savedTask.getName(), "Имя задачи не совпадает.");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Имя задачи не совпадает.");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статус задачи не совпадает.");

        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addNewEpicWithSubtask() {
        final Epic epic = new Epic(null, "Test addNewEpicWithSubtask epic",
                "Test addNewEpicSubtask epic description");
        final int epicId = taskManager.addNewEpic(epic);
        final Subtask subtask = new Subtask(null, "Test addNewSubtask", "Test addNewSubtask description",
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
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    public void updateTask() {
        final Task old = new Task(null, "Старая задача из теста updateTask", "Описание старой задачи",
                Status.NEW);
        final int id = taskManager.addNewTask(old);
        final Task updated = new Task(id, "Новая задача из теста updateTask", "Описание новой задачи",
                Status.IN_PROGRESS);
        taskManager.updateTask(updated);
        assertEquals(updated, taskManager.getTaskById(id), "Задача не обновляется.");
    }

    @Test
    public void updateEpic() {
        final Epic old = new Epic(null, "Старый эпик из теста updateEpic", "Описание старого эпика");
        final int id = taskManager.addNewEpic(old);
        Epic updated = new Epic(id, "Новый эпик из теста updateEpic", "Описание нового эпика");
        taskManager.updateEpic(updated);
        assertEquals(updated, taskManager.getEpicById(id), "Эпик не обновляется.");
    }

    @Test
    public void updateSubtask() {
        final Epic epic = new Epic(null, "Новый эпик из теста updateSubtask", "Описание нового эпика");
        final int epicId = taskManager.addNewEpic(epic);
        final Subtask old = new Subtask(null, "Старая подзадача из теста updateSubtask",
                "Описание старой подзадачи", Status.NEW, epicId);
        final int subtaskId = taskManager.addNewSubtask(old);
        final Subtask updated = new Subtask(subtaskId, "Обновленная подзадача из теста updateSubtask",
                "Описание обновленной подзадачи", Status.IN_PROGRESS, epicId);
        taskManager.updateSubtask(updated);

        assertEquals(updated, taskManager.getSubtaskById(subtaskId), "Подзадача не обновляется.");
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика не обновился вместе с подзадачей.");
    }

    @Test
    public void deleteTask() {
        final Task task = new Task(null, "Задача из теста deleteTask", "Описание задчи", Status.NEW);
        final int id = taskManager.addNewTask(task);
        taskManager.deleteTask(id);
        assertNull(taskManager.getTaskById(id), "Задача не удаляется.");
        assertEquals(0, taskManager.getAllTasks().size(), "Список задач не пустой.");
    }

    @Test
    public void deleteEpic() {
        final Epic epic = new Epic(null, "Новый эпик из теста deleteEpic", "Описание эпика");
        final int epicId = taskManager.addNewEpic(epic);
        final Subtask subtask = new Subtask(null, "Подзадача из теста deleteEpic",
                "Описание подзадачи", Status.NEW, epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        taskManager.deleteEpic(epicId);

        assertNull(taskManager.getEpicById(epicId), "Эпик не удаляется.");
        assertNull(taskManager.getSubtaskById(subtaskId), "Подзадача не удаляется вместе с эпиком.");
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков не пустой.");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список подзадач не пустой.");
    }

    @Test
    public void deleteSubtask() {
        final Epic epic = new Epic(null, "Новый эпик из теста deleteSubtask", "Описание эпика");
        final int epicId = taskManager.addNewEpic(epic);
        final Subtask subtask = new Subtask(null, "Подзадача из теста deleteSubtask",
                "Описание подзадачи", Status.DONE, epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        taskManager.deleteSubtask(subtaskId);

        assertNull(taskManager.getSubtaskById(subtaskId), "Подзадача не удаляется.");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список подзадач не пустой.");
        assertEquals(0, epic.getSubtaskIds().size(), "Не удалился id подзадачи у эпика.");
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика не стал NEW после удаления подзадачи.");
    }

    @Test
    public void deleteAllTasks() {
        final Task task1 = new Task(null, "Задача 1 из теста deleteTask", "Описание задчи 1", Status.NEW);
        final Task task2 = new Task(null, "Задача 2 из теста deleteTask", "Описание задчи 2", Status.IN_PROGRESS);
        final int task1Id = taskManager.addNewTask(task1);
        final int task2Id = taskManager.addNewTask(task2);
        taskManager.deleteAllTasks();

        assertNull(taskManager.getTaskById(task1Id), "Задача 1 не удалилась.");
        assertNull(taskManager.getTaskById(task2Id), "Задача 2 не удалилась.");
        assertEquals(0, taskManager.getAllTasks().size(), "Список задач не пустой.");
    }

    @Test
    public void deleteAllSubtasks() {
        final Epic epic1 = new Epic(null, "Эпик 1 из теста deleteAllSubtasks", "Описание эпика 1");
        final Epic epic2 = new Epic(null, "Эпик 2 из теста deleteAllSubtasks", "Описание эпика 2");
        final int epic1Id = taskManager.addNewEpic(epic1);
        final int epic2Id = taskManager.addNewEpic(epic2);

        final Subtask subtask1 = new Subtask(null, "Подзадача эпика 1 из теста deleteAllSubtasks",
                "Описание подзадачи эпика 1", Status.DONE, epic1Id);
        final Subtask subtask2 = new Subtask(null, "Подзадача эпика 2 из теста deleteAllSubtasks",
                "Описание подзадачи эпика 2", Status.NEW, epic2Id);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);
        final int subtask2Id = taskManager.addNewSubtask(subtask2);

        taskManager.deleteAllSubtasks();

        assertNull(taskManager.getSubtaskById(subtask1Id), "Подзадача эпика 1 не удалилась.");
        assertNull(taskManager.getSubtaskById(subtask2Id), "Подзадача эпика 2 не удалилась.");
        assertEquals(0, epic1.getSubtaskIds().size(), "Список id подзадач эпика 1 не пустой.");
        assertEquals(0, epic2.getSubtaskIds().size(), "Список id подзадач эпика 2 не пустой.");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список подзадач не пустой.");
    }

    @Test
    public void deleteAllEpics() {
        final Epic epic1 = new Epic(null, "Эпик 1 из теста deleteAllEpics", "Описание эпика 1");
        final Epic epic2 = new Epic(null, "Эпик 2 из теста deleteAllEpics", "Описание эпика 2");
        final int epic1Id = taskManager.addNewEpic(epic1);
        final int epic2Id = taskManager.addNewEpic(epic2);

        final Subtask subtask1 = new Subtask(null, "Подзадача эпика 1 из теста deleteAllEpics",
                "Описание подзадачи эпика 1", Status.DONE, epic1Id);
        final Subtask subtask2 = new Subtask(null, "Подзадача эпика 2 из теста deleteAllEpics",
                "Описание подзадачи эпика 2", Status.NEW, epic2Id);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);
        final int subtask2Id = taskManager.addNewSubtask(subtask2);

        taskManager.deleteAllEpics();

        assertNull(taskManager.getEpicById(epic1Id), "Эпик 1 не удалилился.");
        assertNull(taskManager.getEpicById(epic2Id), "Эпик 2 не удалилился.");
        assertNull(taskManager.getSubtaskById(subtask1Id), "Подзадача эпика 1 не удалилась.");
        assertNull(taskManager.getSubtaskById(subtask2Id), "Подзадача эпика 2 не удалилась.");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список подзадач не пустой.");
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков не пустой.");
    }
}