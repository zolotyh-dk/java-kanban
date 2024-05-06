package ru.yandex.javacourse.zolotyh.schedule.manager.task_manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.exception.InvalidTaskException;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;
import ru.yandex.javacourse.zolotyh.schedule.util.TaskUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.javacourse.zolotyh.schedule.util.TaskUtil.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected List<Task> testTasks;
    protected List<Epic> testEpics;
    protected List<Subtask> testSubtasks;

    @BeforeEach
    public void beforeEach() {
        // Наполняю хранилище копиями задач из списков тестовых задач.
        // Копии нужны, чтобы ссылки тестовых задачи и задач, которые лежат в менеджере были разными.
        // Иначе пройдут любые тесты.
        testTasks = TaskUtil.getTestTasks();
        testTasks.forEach(task -> taskManager.addNewTask(new Task(null, task.getName(), task.getDescription(),
                task.getStatus(), task.getDuration(), task.getStartTime())));

        testEpics = TaskUtil.getTestEpics();
        testEpics.forEach(epic -> taskManager.addNewEpic(new Epic(null, epic.getName(), epic.getDescription())));


        testSubtasks = TaskUtil.getTestSubtasks();
        testSubtasks.forEach(subtask -> taskManager.addNewSubtask(new Subtask(null, subtask.getName(),
                subtask.getDescription(), subtask.getStatus(), subtask.getDuration(), subtask.getStartTime(), subtask.getEpicId())));

    }

    @Test
    public void getAllTasksTest() {
        final List<Task> allTasks = taskManager.getAllTasks();
        assertEquals(3, allTasks.size(), "Размер списка задач не совпадает.");
        assertEquals(testTasks.get(0), allTasks.get(0), "Задача 1 не совпадает.");
        assertEquals(testTasks.get(1), allTasks.get(1), "Задача 2 не совпадает.");
        assertEquals(testTasks.get(2), allTasks.get(2), "Задача 3 не совпадает.");
    }

    @Test
    public void getAllEpicsTest() {
        final List<Epic> allEpics = taskManager.getAllEpics();
        assertEquals(2, allEpics.size(), "Размер списка эпиков не совпадает.");
        assertEquals(testEpics.get(0), allEpics.get(0), "Эпик c тремя подзадачами не совпадает.");
        assertEquals(testEpics.get(1), allEpics.get(1), "Эпик без подзадач не совпадает.");
    }

    @Test
    public void getAllSubtasksTest() {
        final List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        assertEquals(3, allSubtasks.size(), "Размер списка подзадач не совпадает.");
        assertEquals(testSubtasks.get(0), allSubtasks.get(0), "Подзадача 1 не совпадает.");
        assertEquals(testSubtasks.get(1), allSubtasks.get(1), "Подзадача 2 не совпадает.");
        assertEquals(testSubtasks.get(2), allSubtasks.get(2), "Подзадача 3 не совпадает.");
    }

    @Test
    public void deleteAllTasks() {
        taskManager.deleteAllTasks();
        assertNull(taskManager.getTaskById(testTasks.get(0).getId()), "Задача 1 не удалилась.");
        assertNull(taskManager.getTaskById(testTasks.get(1).getId()), "Задача 2 не удалилась.");
        assertNull(taskManager.getTaskById(testTasks.get(2).getId()), "Задача 3 не удалилась.");
        assertEquals(0, taskManager.getAllTasks().size(), "Список задач не пустой.");
    }

    @Test
    public void deleteAllSubtasks() {
        taskManager.deleteAllSubtasks();
        assertNull(taskManager.getSubtaskById(testSubtasks.get(0).getId()), "Подзадача 1 не удалилась.");
        assertNull(taskManager.getSubtaskById(testSubtasks.get(1).getId()), "Подзадача 2 не удалилась.");
        assertNull(taskManager.getSubtaskById(testSubtasks.get(2).getId()), "Подзадача 3 не удалилась.");
        assertEquals(0, testEpics.get(0).getSubtaskIds().size(), "Список id подзадач эпика с тремя подзадачами не пустой.");
        assertEquals(0, testEpics.get(1).getSubtaskIds().size(), "Список id подзадач эпика без подзадач не пустой.");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список всех подзадач не пустой.");
    }

    @Test
    public void deleteAllEpics() {
        taskManager.deleteAllEpics();
        assertNull(taskManager.getEpicById(testEpics.get(0).getId()), "Эпик с тремя подзадачами не удалилился.");
        assertNull(taskManager.getEpicById(testEpics.get(1).getId()), "Эпик без подзадач не удалилился.");
        assertNull(taskManager.getSubtaskById(testSubtasks.get(0).getId()), "Подзадача 1 не удалилась.");
        assertNull(taskManager.getSubtaskById(testSubtasks.get(1).getId()), "Подзадача 2 не удалилась.");
        assertNull(taskManager.getSubtaskById(testSubtasks.get(2).getId()), "Подзадача 3 не удалилась.");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список всех подзадач не пустой.");
        assertEquals(0, taskManager.getAllEpics().size(), "Список всех эпиков не пустой.");
    }

    @Test
    public void getTaskByIdTest() {
        final Task expected = testTasks.get(0);
        final Task actual = taskManager.getTaskById(expected.getId());
        assertEquals(expected, actual, "Задачи не совпадают.");
        assertEquals(expected.getName(), actual.getName(), "Имена задач не совпадают.");
        assertEquals(expected.getDescription(), actual.getDescription(), "Описания подзадач не совпадают.");
        assertEquals(expected.getStatus(), actual.getStatus(), "Статусы задач не совпадают.");
        assertEquals(expected.getDuration(), actual.getDuration(), "Продолжительности задач не совпадают.");
        assertEquals(expected.getStartTime(), actual.getStartTime(), "Времена начала задач не совпадают.");
    }

    @Test
    public void getEpicByIdTest() {
        final Epic expected = testEpics.get(0);
        final Epic actual = taskManager.getEpicById(expected.getId());
        assertEquals(expected, actual, "Эпики не совпадают.");
        assertEquals(expected.getName(), actual.getName(), "Иена эпиков не совпадают.");
        assertEquals(expected.getDescription(), actual.getDescription(), "Описания эпиков не совпадают.");
    }

    @Test
    public void getSubtaskById() {
        final Subtask expected = testSubtasks.get(0);
        final Subtask actual = taskManager.getSubtaskById(expected.getId());
        assertEquals(expected, actual, "Подзадачи не совпадают.");
        assertEquals(expected.getName(), actual.getName(), "Имена подзадач не совпадают.");
        assertEquals(expected.getDescription(), actual.getDescription(), "Описания подзадач не совпадают.");
        assertEquals(expected.getStatus(), actual.getStatus(), "Статусы подзадач не совпадают.");
        assertEquals(expected.getDuration(), actual.getDuration(), "Продолжительности подзадач не совпадают.");
        assertEquals(expected.getStartTime(), actual.getStartTime(), "Времена начала подзадач не совпадают.");
    }

    @Test
    public void addNewTaskTest() {
        final Task task = getNewTask();
        final int id = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTaskById(id);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(task.getName(), savedTask.getName(), "Имя задачи не совпадает.");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описание подзадачи не совпадает.");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статус задачи не совпадает.");
        assertEquals(task.getDuration(), savedTask.getDuration(), "Продолжительность задачи не совпадает.");
        assertEquals(task.getStartTime(), savedTask.getStartTime(), "Время начала задачи не совпадает.");
    }

    @Test
    public void addNewEpicTest() {
        final Epic epic = getNewEpic();
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(epic.getName(), savedEpic.getName(), "Имя эпика не совпадает.");
        assertEquals(epic.getDescription(), savedEpic.getDescription(), "Описание эпика не совпадает.");
        assertEquals(Status.NEW, savedEpic.getStatus(), "Эпику не присвоился статус NEW.");
    }

    @Test
    public void addNewSubtaskTest() {
        final Subtask subtask = getNewSubtask();
        final int id = taskManager.addNewSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtaskById(id);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
        assertEquals(subtask.getName(), savedSubtask.getName(), "Имя подзадачи не совпадает.");
        assertEquals(subtask.getDescription(), savedSubtask.getDescription(), "Описание подзадачи не совпадает.");
        assertEquals(subtask.getStatus(), savedSubtask.getStatus(), "Статус подзадачи не совпадает.");
        assertEquals(subtask.getDuration(), savedSubtask.getDuration(), "Продолжительность подзадачи не совпадает.");
        assertEquals(subtask.getStartTime(), savedSubtask.getStartTime(), "Время начала подзадачи не совпадает.");
        assertEquals(subtask.getEpicId(), savedSubtask.getEpicId(), "id эпика не совпадает.");
    }

    @Test
    public void updateTaskTest() {
        final Task updated = getNewTask();
        final int id = testTasks.get(0).getId();
        updated.setId(id);
        taskManager.updateTask(updated);
        final Task saved = taskManager.getTaskById(id);
        assertEquals(updated, saved, "Задача не обновляется.");
        assertEquals(updated.getName(), saved.getName(), "Имя задачи не обновилось.");
        assertEquals(updated.getDescription(), saved.getDescription(), "Описание задачи не обновилось.");
        assertEquals(updated.getStatus(), saved.getStatus(), "Статус задачи не обновилось.");
        assertEquals(updated.getDuration(), saved.getDuration(), "Продолжительность задачи не обновилась.");
        assertEquals(updated.getStartTime(), saved.getStartTime(), "Время начала задачи не обновилось.");
    }

    @Test
    public void updateEpicTest() {
        final Epic updated = getNewEpic();
        final int id = testEpics.get(0).getId();
        updated.setId(id);
        taskManager.updateEpic(updated);
        final Epic saved = taskManager.getEpicById(id);
        assertEquals(updated, saved, "Эпик не обновляется.");
        assertEquals(updated.getName(), saved.getName(), "Имя эпика не обновилось.");
        assertEquals(updated.getDescription(), saved.getDescription(), "Описание эпика не обновилось.");
    }

    @Test
    public void updateSubtaskTest() {
        final Subtask updated = getNewSubtask();
        final int id = testSubtasks.get(0).getId();
        updated.setId(id);
        taskManager.updateSubtask(updated);
        final Subtask saved = taskManager.getSubtaskById(id);

        assertEquals(updated, saved, "Подзадача не обновляется.");
        assertEquals(updated.getName(), saved.getName(), "Имя подзадачи не обновилось.");
        assertEquals(updated.getDescription(), saved.getDescription(), "Описание подзадачи не обновилось.");
        assertEquals(updated.getStatus(), saved.getStatus(), "Статус подзадачи не обновился.");
        assertEquals(updated.getDuration(), saved.getDuration(), "Продолжительность подзадачи не обновилась.");
        assertEquals(updated.getStartTime(), saved.getStartTime(), "Время начала подзадачи не обновилось.");
        assertEquals(Status.DONE, taskManager.getEpicById(saved.getEpicId()).getStatus(), "Статус эпика не обновился вместе с подзадачей.");
    }

    @Test
    public void deleteTaskTest() {
        final int id = testTasks.get(0).getId();
        taskManager.deleteTask(id);
        assertNull(taskManager.getTaskById(id), "Задача не удаляется.");
        assertEquals(2, taskManager.getAllTasks().size(), "Размер списка оставшихся зада не соответствует.");
    }

    @Test
    public void deleteEpicTest() {
        final int id = testEpics.get(0).getId();
        taskManager.deleteEpic(id);
        assertNull(taskManager.getEpicById(id), "Эпик не удаляется.");
        assertEquals(1, taskManager.getAllEpics().size(), "Размер списка эпиков не соответствует.");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Размер списка подзадач не соответвствует.");
    }

    @Test
    public void deleteSubtaskTest() {
        final Subtask subtask = testSubtasks.get(0);
        final Epic epic = taskManager.getEpicById(subtask.getEpicId());
        taskManager.deleteSubtask(subtask.getId());
        assertNull(taskManager.getSubtaskById(subtask.getId()), "Подзадача не удаляется.");
        assertEquals(2, taskManager.getAllSubtasks().size(), "Размер списка подзадач соответствует.");
        assertEquals(2, epic.getSubtaskIds().size(), "Размер списка id подзадач у эпика не соответствует.");
        assertEquals(Status.DONE, epic.getStatus(), "Статус эпика не стал DONE после удаления подзадачи.");
    }

    @Test
    public void getSubtasksByEpicTest() {
        final Epic savedEpic = taskManager.getEpicById(testEpics.get(0).getId());
        final List<Subtask> subtasksByEpic = taskManager.getSubtasksByEpic(savedEpic);
        assertEquals(testSubtasks.size(), subtasksByEpic.size(), "Размер списка подзадач не совпадает.");
        for (int i = 0; i < subtasksByEpic.size(); i++) {
            assertEquals(testSubtasks.get(i), subtasksByEpic.get(i), "Подзадача с индексом " + i + " не совпадает.");
        }
    }

    @Test
    public void getPrioritizedTasks() {
        final List<Task> expected = new ArrayList<>(testTasks);
        expected.addAll(testSubtasks);
        expected.sort(Comparator.comparing(Task::getStartTime));
        final List<Task> actual = taskManager.getPrioritizedTasks();
        assertEquals(expected.size(), actual.size(), "Размер списка отсортированных задач не совпадает.");
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i), actual.get(i), "Задача с индексом " + i + " не совпадает.");
        }
    }

    @Test
    public void shouldThrowsWhenTaskTimeIntersected() {
        getTasksWithTimeIntersection()
                .forEach(task -> assertThrows(InvalidTaskException.class, () -> taskManager.addNewTask(task),
                        "Пересечение новой задачи по времени с существующей задачей не вызвало выброс исключения."));
    }

    @Test
    public void epicStatusShouldBeNewWhenAllSubtasksNew() {
        final List<Subtask> subtasks = generateSubtasksWithStatus(3, 0, 0, 5);
        subtasks.forEach(taskManager::addNewSubtask);
        final Epic epic = taskManager.getEpicById(5);
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW если все подзадачи NEW.");
    }

    @Test
    public void epicStatusShouldBeDoneWhenAllSubtasksDone() {
        final List<Subtask> subtasks = generateSubtasksWithStatus(0, 0, 3, 5);
        subtasks.forEach(taskManager::addNewSubtask);
        final Epic epic = taskManager.getEpicById(5);
        assertEquals(Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE если все подзадачи DONE.");
    }

    @Test
    public void epicStatusShouldBeInProgressWhenSubtasksNewAndDone() {
        final List<Subtask> subtasks = generateSubtasksWithStatus(1, 0, 1, 5);
        subtasks.forEach(taskManager::addNewSubtask);
        final Epic epic = taskManager.getEpicById(5);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS," +
                " если подзадачи NEW и DONE.");
    }

    @Test
    public void epicStatusShouldBeInProgressWhenSubtasksInProgress() {
        final List<Subtask> subtasks = generateSubtasksWithStatus(0, 3, 0, 5);
        subtasks.forEach(taskManager::addNewSubtask);
        final Epic epic = taskManager.getEpicById(5);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS," +
                " если все подзадачи IN_PROGRESS.");
    }
}
