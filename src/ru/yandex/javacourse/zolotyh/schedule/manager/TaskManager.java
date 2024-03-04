package ru.yandex.javacourse.zolotyh.schedule.manager;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int nextId = 0;
    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
        }
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public int saveOrUpdate(Task task) {
        if (task.getId() == null) {
            task.setId(nextId++);
        }
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public int saveOrUpdate(Subtask subtask) {
        if (subtask.getId() == null) {
            subtask.setId(nextId++);
        }
        subtasks.put(subtask.getId(), subtask);

        Epic epic = subtask.getEpic();
        int index = epic.getSubtasks().indexOf(subtask);
        if (index == -1) {
            epic.getSubtasks().add(subtask);
        } else {
            epic.getSubtasks().set(index, subtask);
        }
        saveOrUpdate(epic);
        return subtask.getId();
    }

    public int saveOrUpdate(Epic epic) {
        if (epic.getId() == null) {
            epic.setId(nextId++);
        }

        if (epic.getSubtasks() == null || epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else if (epic.isAllSubtasksDone()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = subtask.getEpic();
        epic.getSubtasks().remove(subtask);
        saveOrUpdate(epic);
        subtasks.remove(id);
    }

    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }
}
