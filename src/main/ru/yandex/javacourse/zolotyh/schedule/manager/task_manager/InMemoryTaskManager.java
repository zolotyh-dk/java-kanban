package ru.yandex.javacourse.zolotyh.schedule.manager.task_manager;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.exception.InvalidTaskException;
import ru.yandex.javacourse.zolotyh.schedule.manager.history_manager.HistoryManager;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;
import ru.yandex.javacourse.zolotyh.schedule.util.Managers;
import ru.yandex.javacourse.zolotyh.schedule.util.TimeUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int generatorId = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().stream()
                .peek(historyManager::remove)
                .map(tasks::get)
                .forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        subtasks.keySet().stream()
                .peek(historyManager::remove)
                .map(subtasks::get)
                .forEach(prioritizedTasks::remove);
        subtasks.clear();

        epics.keySet().forEach(historyManager::remove);
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        epics.values().stream()
                .peek(Epic::clearSubtaskIds)
                .map(Epic::getId)
                .forEach(this::updateEpicFields);

        subtasks.keySet().stream()
                .peek(historyManager::remove)
                .map(subtasks::get)
                .forEach(prioritizedTasks::remove);

        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public int addNewTask(Task task) {
        if (TimeUtil.isTimeIntersections(task, getPrioritizedTasks())) {
            throw new InvalidTaskException("Время выполнения новой задачи уже занято другой задачей.");
        }
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        addPrioritizedTask(task);
        return id;
    }

    @Override
    public void updateTask(Task task) {
        if (TimeUtil.isTimeIntersections(task, getPrioritizedTasks())) {
            throw new InvalidTaskException("Время выполнения обновленной задачи уже занято другой задачей.");
        }
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
        addPrioritizedTask(task);
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        if (TimeUtil.isTimeIntersections(subtask, getPrioritizedTasks())) {
            throw new InvalidTaskException("Время выполнения новой подзадачи уже занято другой задачей.");
        }
        final int epicId = subtask.getEpicId();
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        final int id = ++generatorId;
        subtask.setId(id);
        subtasks.put(id, subtask);
        addPrioritizedTask(subtask);
        epic.getSubtaskIds().add(id);
        updateEpicFields(epicId);
        return subtask.getId();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (TimeUtil.isTimeIntersections(subtask, getPrioritizedTasks())) {
            throw new InvalidTaskException("Время выполнения обновленной подзадачи уже занято другой задачей.");
        }
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
        addPrioritizedTask(subtask);
        updateEpicFields(epicId);
    }

    @Override
    public int addNewEpic(Epic epic) {
        final int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        updateEpicFields(id);
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        final int id = epic.getId();
        final Epic savedEpic = epics.get(id);
        if (savedEpic == null) {
            return;
        }
        epic.setSubtaskIds(savedEpic.getSubtaskIds());
        epic.setStatus(savedEpic.getStatus());
        epics.put(id, epic);
    }

    @Override
    public void deleteTask(int id) {
        final Task task = tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(task);
    }

    @Override
    public void deleteEpic(int id) {
        final Epic epic = epics.get(id);
        historyManager.remove(id);

        epic.getSubtaskIds().stream()
                .peek(historyManager::remove)
                .map(subtasks::remove)
                .forEach(prioritizedTasks::remove);

        epics.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        final Subtask subtask = subtasks.remove(id);
        prioritizedTasks.remove(subtask);
        historyManager.remove(id);
        if (subtask == null) {
            return;
        }
        final Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicFields(epic.getId());
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void addPrioritizedTask(Task task) {
        if (task == null || task.getStartTime() == null) {
            return;
        }
        prioritizedTasks.add(task);
    }

    protected void addAnyTask(Task task) {
        switch (task.getType()) {
            case EPIC:
                epics.put(task.getId(), (Epic) task);
                break;
            case SUBTASK:
                subtasks.put(task.getId(), (Subtask) task);
                addPrioritizedTask(task);
                break;
            default:
                tasks.put(task.getId(), task);
                addPrioritizedTask(task);
                break;
        }
    }

    private void updateEpicFields(int epicId) {
        final Epic epic = epics.get(epicId);
        final List<Integer> subs = epic.getSubtaskIds();

        final Status status = calculateEpicStatusFromSubtasks(subs);
        final Duration duration = calculateDurationsSum(subs);
        final LocalDateTime startTime = findEarliestStartTime(subs);
        final LocalDateTime endTime = findLatestEndTime(subs);

        epic.setStatus(status);
        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }

    private Status calculateEpicStatusFromSubtasks(List<Integer> subtaskIds) {
        return subtaskIds.stream()
                .map(subtasks::get)
                .map(Task::getStatus)
                .reduce((status1, status2) ->
                        status1.equals(status2) && !status1.equals(Status.IN_PROGRESS) ? status1 : Status.IN_PROGRESS)
                .orElse(Status.NEW);
    }

    private Duration calculateDurationsSum(List<Integer> subtaskIds) {
        return subtaskIds.stream()
                .map(subtasks::get)
                .map(Task::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration::plus)
                .orElse(null);
    }

    private LocalDateTime findEarliestStartTime(List<Integer> subtaskIds) {
        return subtaskIds.stream()
                .map(subtasks::get)
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private LocalDateTime findLatestEndTime(List<Integer> subtaskIds) {
        return subtaskIds.stream()
                .map(subtasks::get)
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }
}