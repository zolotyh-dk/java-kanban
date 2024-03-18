package ru.yandex.javacourse.zolotyh.schedule;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.manager.HistoryManager;
import ru.yandex.javacourse.zolotyh.schedule.manager.TaskManager;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;
import ru.yandex.javacourse.zolotyh.schedule.util.Managers;

public class Main {
    private static final String DELIMITER = "-".repeat(150) + '\n';
    private static final TaskManager taskManager = Managers.getDefault();
    private static final HistoryManager historyManager = taskManager.getHistoryManager();

    public static void main(String[] args) {
        initialiseRepository();
        printAllTasks();
        showHistory();
    }

    public static void initialiseRepository() {
        Epic epic = new Epic(null, "Эпик", "Описание эпика");
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask(null, "Подзадача 1", "Описание подзадачи 1", Status.IN_PROGRESS, epic.getId());
        Subtask subtask2 = new Subtask(null, "Подзадача 2", "Описание подзадачи 2", Status.DONE, epic.getId());
        Subtask subtask3 = new Subtask(null, "Подзадача 3", "Описание подзадачи 3", Status.NEW, epic.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);
        Task task1 = new Task(null, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(null, "Задача 2", "Описание задачи 2", Status.DONE);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
    }

    private static void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : taskManager.getSubtasksByEpic(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println(DELIMITER);
    }

    private static void showHistory() {
        System.out.println("Просмотрим задачу:");
        int taskId = taskManager.getAllTasks().get(0).getId();
        System.out.println(taskManager.getTaskById(taskId));
        System.out.println(DELIMITER);

        System.out.println("Просмотрим эпик:");
        int epicId = taskManager.getAllEpics().get(0).getId();
        System.out.println(taskManager.getEpicById(epicId));
        System.out.println(DELIMITER);

        System.out.println("Просмотрим подзадачу:");
        int subtaskId = taskManager.getAllSubtasks().get(0).getId();
        System.out.println(taskManager.getSubtaskById(subtaskId));
        System.out.println(DELIMITER);

        System.out.println("История просмотров:");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
    }
}
