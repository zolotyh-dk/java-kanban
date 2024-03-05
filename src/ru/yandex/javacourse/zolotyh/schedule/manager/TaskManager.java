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
            epic.getSubtaskIds().clear();
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
            task.setId(++generatorId);
        }
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public int saveOrUpdate(Subtask subtask) {
        if (subtask.getId() == null) {
            subtask.setId(++generatorId);
        }
        subtasks.put(subtask.getId(), subtask);

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        int subtaskId = subtask.getId();
        int index = epic.getSubtaskIds().indexOf(subtaskId);
        if (index == -1) {
            epic.getSubtaskIds().add(subtaskId);
        } else {
            epic.getSubtaskIds().set(index, subtaskId);
        }
        saveOrUpdate(epic);
        return subtask.getId();
    }

    public int saveOrUpdate(Epic epic) {
        if (epic.getId() == null) {
            epic.setId(++generatorId);
        }
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        return epic.getId();
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

    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        epic.getSubtaskIds().remove(Integer.valueOf(id));
        saveOrUpdate(epic);
        subtasks.remove(id);
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
