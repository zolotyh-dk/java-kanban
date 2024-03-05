package ru.yandex.javacourse.zolotyh.schedule;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.manager.TaskManager;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;
import ru.yandex.javacourse.zolotyh.schedule.util.TaskUtil;

import static ru.yandex.javacourse.zolotyh.schedule.util.TaskUtil.*;

public class Main {
    private static final String DELIMITER = "-".repeat(150);

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        addNewTaskTest(TaskUtil.task1, taskManager);
        updateTaskTest(task2, taskManager);

        addNewEpicTest(epic1, taskManager);


        addNewSubtaskTest(subtask1, taskManager);
        addNewSubtaskTest(subtask2, taskManager);

        updateEpicTest(epic2, taskManager);
        updateSubtaskTest(subtask3, taskManager);

        getSubtasksByEpicTest(epic1, taskManager);

        getAllTasksTest(taskManager);
        getAllEpicsTest(taskManager);
        getAllSubtasksTest(taskManager);

        removeTaskByIdTest(task1.getId(), taskManager);
        removeEpicByIdTest(epic1.getId(), taskManager);
        removeSubtaskByIdTest(subtask3.getId(), taskManager);

        removeAllTasksTest(taskManager);
        removeAllSubtasksTest(taskManager);
        removeAllEpicsTest(taskManager);
    }

    private static void addNewTaskTest(Task task, TaskManager manager) {
        System.out.println("addNewTaskTest");
        int id = manager.addNewTask(task);
        System.out.println("Сохранено: " + manager.getTaskById(id));
        System.out.println(DELIMITER);
    }

    private static void updateTaskTest(Task task, TaskManager manager) {
        System.out.println("updateTaskTest");
        int id = manager.addNewTask(task);
        Task updated = new Task(id,
                task.getName(),
                task.getDescription() + " А ещё с сокрытием.",
                Status.DONE);
        manager.updateTask(updated);
        System.out.println("Обновлено: " + manager.getTaskById(id));
        System.out.println(DELIMITER);
    }

    private static void addNewEpicTest(Epic epic, TaskManager manager) {
        System.out.println("saveEpicTest");
        int id = manager.addNewEpic(epic);
        System.out.println("Сохранено: " + manager.getEpicById(id));
        System.out.println(DELIMITER);
    }

    private static void updateEpicTest(Epic epic, TaskManager manager) {
        System.out.println("updateEpicTest");
        int id = manager.addNewEpic(epic);
        Epic updated = new Epic(id, "Kanban-board iOS application ", epic.getDescription());
        manager.updateEpic(updated);
        System.out.println("Обновлено: " + manager.getEpicById(id));
        System.out.println(DELIMITER);
    }

    private static void addNewSubtaskTest(Subtask subtask, TaskManager manager) {
        System.out.println("saveSubtaskTest");
        int id = manager.addNewSubtask(subtask);
        System.out.println("Сохранено: " + manager.getSubtaskById(id);
        System.out.println(DELIMITER);
    }

    private static void updateSubtaskTest(Subtask subtask, TaskManager manager) {
        System.out.println("updateSubtaskTest");
        manager.addNewSubtask(subtask);
        int id = subtask.getId();
        Subtask updated = new Subtask(id, subtask.getName(), subtask.getDescription(), Status.DONE, subtask.getEpicId());
        manager.updateSubtask(updated);
        System.out.println("Обновлено: " + manager.getSubtaskById(id));
        System.out.println(DELIMITER);
    }

    private static void getAllTasksTest(TaskManager manager) {
        System.out.println("getAllTasksTest()");
        manager.getAllTasks().forEach(System.out::println);
        System.out.println(DELIMITER);
    }

    private static void getAllEpicsTest(TaskManager manager) {
        System.out.println("getAllEpicsTest");
        manager.getAllEpics().forEach(System.out::println);
        System.out.println(DELIMITER);
    }

    public static void getAllSubtasksTest(TaskManager manager) {
        System.out.println("getAllSubtasksTest");
        manager.getAllSubtasks().forEach(System.out::println);
        System.out.println(DELIMITER);
    }

    public static void removeTaskByIdTest(int id, TaskManager manager) {
        System.out.println("removeTaskByIdTest");
        manager.deleteTask(id);
        System.out.println("После удаления задачи: ");
        manager.getAllTasks().forEach(System.out::println);
        System.out.println(DELIMITER);
    }

    public static void removeEpicByIdTest(int id, TaskManager manager) {
        System.out.println("removeEpicByIdTest");
        manager.deleteEpic(id);
        System.out.println("Все эпики после удаления:");
        manager.getAllEpics().forEach(System.out::println);
        System.out.println("Все подзадачи после удаления эпика:");
        manager.getAllSubtasks().forEach(System.out::println);
        System.out.println(DELIMITER);
    }

    public static void removeSubtaskByIdTest(int id, TaskManager manager) {
        System.out.println("removeSubtaskByIdTest");
        manager.deleteSubtask(id);
        System.out.println("Все подзадачи после удаления:");
        manager.getAllSubtasks().forEach(System.out::println);
        System.out.println("Все эпики после удаления подзадачи:");
        manager.getAllEpics().forEach(System.out::println);
        System.out.println(DELIMITER);
    }

    public static void removeAllTasksTest(TaskManager manager) {
        System.out.println("removeAllTasksTest");
        manager.deleteAllTasks();
        System.out.println("Удалили все задачи. Задач осталось: " + manager.getAllTasks().size());
        System.out.println(DELIMITER);
    }

    public static void removeAllSubtasksTest(TaskManager manager) {
        System.out.println("removeAllSubtasksTest");
        manager.deleteAllSubtasks();
        System.out.println("Удалили все позадачи. Подзадач осталось: " + manager.getAllSubtasks().size());
        System.out.println("Все эпики после удаления подзадач:");
        manager.getAllEpics().forEach(System.out::println);
        System.out.println(DELIMITER);
    }

    public static void removeAllEpicsTest(TaskManager manager) {
        System.out.println("removeAllEpicsTest");
        manager.deleteAllEpics();
        System.out.println("Удалили все эпики. Эпиков осталось: " + manager.getAllEpics().size());
        System.out.println("Подзадач осталось: " + manager.getAllSubtasks().size());
        System.out.println(DELIMITER);
    }

    public static void getSubtasksByEpicTest(Epic epic, TaskManager manager) {
        System.out.println("getSubtasksByEpicTest");
        System.out.println("Для эпика: " + epic);
        System.out.println("все подзадачи: ");
        manager.getSubtasksByEpic(epic).forEach(System.out::println);
        System.out.println(DELIMITER);
    }
}
