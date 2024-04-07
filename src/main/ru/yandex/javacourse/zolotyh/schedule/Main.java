package ru.yandex.javacourse.zolotyh.schedule;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.manager.TaskManager;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;
import ru.yandex.javacourse.zolotyh.schedule.util.Managers;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Создание менеджера задач и истории
        TaskManager taskManager = Managers.getDefault();

        // Создание задач
        Task task1 = new Task(null, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(null, "Задача 2", "Описание задачи 2", Status.IN_PROGRESS);

        // Добавление задач в менеджер истории
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        // Создание эпика c тремя подзадачами
        Epic epic = new Epic(null, "Эпик с тремя подзадачами", "Описание эпика с тремя подзадачами");
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask(null, "Подзадача 1", "Описание подзадачи 1", Status.NEW, epic.getId());
        Subtask subtask2 =new Subtask(null, "Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, epic.getId());
        Subtask subtask3 =new Subtask(null, "Подзадача 3", "Описание подзадачи 3", Status.DONE, epic.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        // Создание эпика без подзадач
        Epic epicWithoutSubtasks = new Epic(null, "Эпик без подзадач", "Описание эпика без подзадач");
        taskManager.addNewEpic(epicWithoutSubtasks);

        // Запрос задач несколько раз в разном порядке
        taskManager.getTaskById(task1.getId());
        System.out.println("История после запроса Задачи 1:");
        printHistory(taskManager.getHistory());

        taskManager.getEpicById(epic.getId());
        System.out.println("История после запроса Эпика с тремя подзадачами:");
        printHistory(taskManager.getHistory());

        taskManager.getTaskById(task2.getId());
        System.out.println("История после запроса Задачи 2:");
        printHistory(taskManager.getHistory());

        taskManager.getSubtaskById(subtask2.getId());
        System.out.println("История после запроса Подзадачи 2:");
        printHistory(taskManager.getHistory());

        taskManager.getEpicById(epicWithoutSubtasks.getId());
        System.out.println("История после запроса Эпиака без подзадач:");
        printHistory(taskManager.getHistory());

        taskManager.getTaskById(task1.getId());
        System.out.println("История после повторного запроса Задачи 1:");
        printHistory(taskManager.getHistory());

        taskManager.getSubtaskById(subtask1.getId());
        System.out.println("История после запроса Подзадачи 1:");
        printHistory(taskManager.getHistory());

        taskManager.getEpicById(epic.getId());
        System.out.println("История после повторного запроса Эпика с тремя подзадачами:");
        printHistory(taskManager.getHistory());

        taskManager.getSubtaskById(subtask2.getId());
        System.out.println("История после повторного запроса Подзадачи 2:");
        printHistory(taskManager.getHistory());

        taskManager.getTaskById(task1.getId());
        System.out.println("История после третьего запроса Задачи 1:");
        printHistory(taskManager.getHistory());

        // Удаление задачи из хранилища
        taskManager.deleteTask(task1.getId());

        // Вывод истории после удаления задачи
        System.out.println("История после удаления Задачи 1:");
        printHistory(taskManager.getHistory());

        // Удаление эпика с тремя подзадачами
        taskManager.deleteEpic(epic.getId());

        // Вывод истории после удаления эпика с подзадачами
        System.out.println("История после удаления Эпика с тремя подзадачами:");
        printHistory(taskManager.getHistory());
    }

    private static void printHistory(List<Task> history) {
        String delimiter = "-".repeat(150);
        for (Task task : history) {
            System.out.println(task);
        }
        System.out.println(delimiter);
    }
}
