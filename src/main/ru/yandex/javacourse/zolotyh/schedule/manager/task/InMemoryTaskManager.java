package ru.yandex.javacourse.zolotyh.schedule.manager.task;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.exception.InvalidTaskException;
import ru.yandex.javacourse.zolotyh.schedule.manager.history.HistoryManager;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;
import ru.yandex.javacourse.zolotyh.schedule.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int generatorId = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected final Map<LocalDateTime, Boolean> slots;

    public InMemoryTaskManager() {
        // Создание таблицы свободных слотов времени на год вперед
        slots = new HashMap<>();
        LocalDateTime current = LocalDateTime.of(2024, Month.MAY, 1, 0, 0);
        final LocalDateTime endOfYear = current.plusYears(1);
        while (current.isBefore(endOfYear)) {
            slots.put(current, false); // Изначально все временные слоты свободны
            current = current.plusMinutes(15);
        }
    }

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
                .peek(task -> freeTimeInterval(task.getStartTime(), task.getEndTime()))
                .forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        subtasks.keySet().stream()
                .peek(historyManager::remove)
                .map(subtasks::get)
                .peek(subtask -> freeTimeInterval(subtask.getStartTime(), subtask.getEndTime()))
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
                .peek(subtask -> freeTimeInterval(subtask.getStartTime(), subtask.getEndTime()))
                .forEach(prioritizedTasks::remove);

        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        final Optional<Task> task = Optional.ofNullable(tasks.get(id));
        task.ifPresent(historyManager::add);
        return task.orElseThrow(() -> new NoSuchElementException("Задачи с id " + id + " не существует."));
    }

    @Override
    public Epic getEpicById(int id) {
        final Optional<Epic> epic = Optional.ofNullable(epics.get(id));
        epic.ifPresent(historyManager::add);
        return epic.orElseThrow(() -> new NoSuchElementException("Эпика с id " + id + " не существует."));
    }

    @Override
    public Subtask getSubtaskById(int id) {
        final Optional<Subtask> subtask = Optional.ofNullable(subtasks.get(id));
        subtask.ifPresent(historyManager::add);
        return subtask.orElseThrow(() -> new NoSuchElementException("Подзадачи с id " + id + " не существует."));
    }

    @Override
    public int addNewTask(Task task) throws InvalidTaskException {
        if (isTimeIntervalBusy(task.getStartTime(), task.getEndTime())) {
            throw new InvalidTaskException("Время выполнения новой задачи уже занято другой задачей.");
        }
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        addPrioritizedTask(task);
        return id;
    }

    @Override
    public void updateTask(Task task) throws InvalidTaskException {
        if (isTimeIntervalBusy(task.getStartTime(), task.getEndTime())) {
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
    public Integer addNewSubtask(Subtask subtask) throws InvalidTaskException {
        if (isTimeIntervalBusy(subtask.getStartTime(), subtask.getEndTime())) {
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
    public void updateSubtask(Subtask subtask) throws InvalidTaskException {
        if (isTimeIntervalBusy(subtask.getStartTime(), subtask.getEndTime())) {
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
        epic.setDuration(savedEpic.getDuration());
        epic.setStartTime(savedEpic.getStartTime());
        epic.setEndTime(savedEpic.getEndTime());
        epics.put(id, epic);
    }

    @Override
    public void deleteTask(int id) {
        final Task task = tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(task);
        freeTimeInterval(task.getStartTime(), task.getEndTime());
    }

    @Override
    public void deleteEpic(int id) {
        final Epic epic = epics.get(id);
        historyManager.remove(id);

        epic.getSubtaskIds().stream()
                .peek(historyManager::remove)
                .map(subtasks::remove)
                .peek(subtask -> freeTimeInterval(subtask.getStartTime(), subtask.getEndTime()))
                .forEach(prioritizedTasks::remove);

        epics.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        final Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        prioritizedTasks.remove(subtask);
        freeTimeInterval(subtask.getStartTime(), subtask.getEndTime());
        historyManager.remove(id);
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
        bookTimeInterval(task.getStartTime(), task.getEndTime());
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

    // Проверка методом наложения отрезков
    private boolean isTimeIntersection(Task newTask, List<Task> existedTasks) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false;
        }
        return existedTasks.stream().anyMatch(existedTask -> isTimeIntersection(newTask, existedTask));
    }

    private boolean isTimeIntersection(Task newTask, Task existedTask) {
        return newTask.getStartTime().isBefore(existedTask.getEndTime()) &&
                newTask.getEndTime().isAfter(existedTask.getStartTime());
    }

    // Проверка через 15-минутные интервалы в HashMap slots
    private boolean isTimeIntervalBusy(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return false;
        }
        LocalDateTime current = start;
        while (current.isBefore(end)) {
            if (!slots.containsKey(current) || slots.get(current)) {
                return true;
            }
            current = current.plusMinutes(15);
        }
        return false;
    }

    private void bookTimeInterval(LocalDateTime start, LocalDateTime end) {
        LocalDateTime current = start;
        while (current.isBefore(end)) {
            slots.put(current, true);
            current = current.plusMinutes(15);
        }
    }

    private void freeTimeInterval(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return;
        }
        LocalDateTime current = start;
        while (current.isBefore(end)) {
            slots.put(current, false);
            current = current.plusMinutes(15);
        }
    }
}