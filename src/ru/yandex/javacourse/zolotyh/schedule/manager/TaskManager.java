package ru.yandex.javacourse.zolotyh.schedule.manager;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int generatorId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

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
            epic.clearSubtaskIds();
            updateEpicStatus(epic.getId());
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

    public int addNewTask(Task task) {
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public void updateTask(Task task) {
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    public int addNewSubtask(Subtask subtask) {
        final int id = ++generatorId;
        subtask.setId(id);
        subtasks.put(id, subtask);
        final int epicId = subtask.getEpicId();
        final Epic epic = epics.get(epicId);
        epic.getSubtaskIds().add(id);
        updateEpicStatus(epicId);
        return subtask.getId();
    }

    public void updateSubtask(Subtask subtask) {
        final int id = subtask.getId();
        final int epicId = subtask.getEpicId();
        final Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask == null) {
            return;
        }
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        subtasks.put(id, subtask);
        updateEpicStatus(epicId);
    }

    public int addNewEpic(Epic epic) {
        final int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        updateEpicStatus(id);
        return id;
    }

    public void updateEpic(Epic epic) {
        final int id = epic.getId();
        final Epic savedEpic = epics.get(id);
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> result = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            result.add(subtasks.get(subtaskId));
        }
        return result;
    }

    private void updateEpicStatus(int id) {
        Epic epic = epics.get(id);
        List<Subtask> subtasksOfEpic = getSubtasksByEpic(epic);
        if (subtasksOfEpic.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else if (isAllSubtasksDone(subtasksOfEpic)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private boolean isAllSubtasksDone(List<Subtask> subtasks) {
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.DONE) {
                return false;
            }
        }
        return true;
    }
}
