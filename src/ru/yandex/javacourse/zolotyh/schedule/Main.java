package ru.yandex.javacourse.zolotyh.schedule;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.manager.TaskManager;
import ru.yandex.javacourse.zolotyh.schedule.task.Epic;
import ru.yandex.javacourse.zolotyh.schedule.task.Subtask;
import ru.yandex.javacourse.zolotyh.schedule.task.Task;
import ru.yandex.javacourse.zolotyh.schedule.util.Managers;

public class Main {
    private static final String DELIMITER = "-".repeat(150);

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task(null,
                "Выучить инкапсуляцию.",
                "Разобраться с модификаторами доступа.",
                Status.NEW);
        addNewTaskTest(task1, taskManager);

        Task task2 = new Task(null,
                "Выучить наследование.",
                "Разобраться с ключевым словом extends и переопределением методов.",
                Status.DONE);
        updateTaskTest(task2, taskManager);

        Epic epic1 = new Epic(null,
                "Приложение канбан-доска",
                "Разработать приложение канбан-доску");
        addNewEpicTest(epic1, taskManager);

        Subtask subtask1 = new Subtask(null,
                "Разработать бэкенд",
                "Написть серверную часть приложения",
                Status.IN_PROGRESS,
                epic1.getId());
        addNewSubtaskTest(subtask1, taskManager);

        Subtask subtask2 = new Subtask(null,
                "Разработать фронтенд",
                "Написать клиентскую часть приложения",
                Status.DONE,
                epic1.getId());
        addNewSubtaskTest(subtask2, taskManager);

        Epic epic2 = new Epic(null,
                "iOS приложение",
                "Разработать iOS приложение для канбан-доски");
        updateEpicTest(epic2, taskManager);
        Subtask subtask3 = new Subtask(null,
                "Выучить swift",
                "Без swift нам никак не написать приложение для iOS",
                Status.NEW,
                epic2.getId());
        updateSubtaskTest(subtask3, taskManager);

        getSubtasksByEpicTest(epic1, taskManager);

        getAllTasksTest(taskManager);
        getAllEpicsTest(taskManager);
        getAllSubtasksTest(taskManager);

        getHistoryCheck(taskManager);

        deleteTaskTest(task1.getId(), taskManager);
        deleteSubtaskTest(subtask1.getId(), taskManager);
        deleteEpicTest(epic1.getId(), taskManager);

        deleteAllTasksTest(taskManager);
        deleteAllSubtasksTest(taskManager);
        deleteAllEpicsTest(taskManager);
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
        System.out.println("addNewEpicTest");
        int id = manager.addNewEpic(epic);
        System.out.println("Сохранено: " + manager.getEpicById(id));
        System.out.println(DELIMITER);
    }

    private static void addNewSubtaskTest(Subtask subtask, TaskManager manager) {
        System.out.println("addNewSubtaskTest");
        int id = manager.addNewSubtask(subtask);
        System.out.println("Сохранено: " + manager.getSubtaskById(id));
        System.out.println("Статус эпика теперь: " + manager.getEpicById(subtask.getEpicId()).getStatus());
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

    private static void updateSubtaskTest(Subtask subtask, TaskManager manager) {
        System.out.println("updateSubtaskTest");
        manager.addNewSubtask(subtask);
        int id = subtask.getId();
        Subtask updated = new Subtask(id, subtask.getName(), subtask.getDescription(), Status.DONE, subtask.getEpicId());
        manager.updateSubtask(updated);
        System.out.println("Обновлено: " + manager.getSubtaskById(id));
        System.out.println("Статус эпика теперь: " + manager.getEpicById(subtask.getEpicId()).getStatus());
        System.out.println(DELIMITER);
    }

    public static void getSubtasksByEpicTest(Epic epic, TaskManager manager) {
        System.out.println("getSubtasksByEpicTest");
        System.out.println("Для эпика: " + epic);
        System.out.println("все подзадачи: ");
        manager.getSubtasksByEpic(epic).forEach(System.out::println);
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

    public static void getHistoryCheck(TaskManager manager) {
        System.out.println("getHistoryCheck");
        manager.getHistory().forEach(System.out::println);
        System.out.println(DELIMITER);
    }

    public static void deleteTaskTest(int id, TaskManager manager) {
        System.out.println("deleteTaskTest");
        manager.deleteTask(id);
        System.out.println("После удаления задачи: ");
        manager.getAllTasks().forEach(System.out::println);
        System.out.println(DELIMITER);
    }

    public static void deleteSubtaskTest(int id, TaskManager manager) {
        System.out.println("deleteSubtaskTest");
        manager.deleteSubtask(id);
        System.out.println("Все подзадачи после удаления:");
        manager.getAllSubtasks().forEach(System.out::println);
        System.out.println("Все эпики после удаления подзадачи:");
        manager.getAllEpics().forEach(System.out::println);
        System.out.println(DELIMITER);
    }

    public static void deleteEpicTest(int id, TaskManager manager) {
        System.out.println("deleteEpicTest");
        manager.deleteEpic(id);
        System.out.println("Все эпики после удаления:");
        manager.getAllEpics().forEach(System.out::println);
        System.out.println("Все подзадачи после удаления эпика:");
        manager.getAllSubtasks().forEach(System.out::println);
        System.out.println(DELIMITER);
    }

    public static void deleteAllTasksTest(TaskManager manager) {
        System.out.println("deleteAllTasksTest");
        manager.deleteAllTasks();
        System.out.println("Удалили все задачи. Задач осталось: " + manager.getAllTasks().size());
        System.out.println(DELIMITER);
    }

    public static void deleteAllSubtasksTest(TaskManager manager) {
        System.out.println("deleteAllSubtasksTest");
        manager.deleteAllSubtasks();
        System.out.println("Удалили все позадачи. Подзадач осталось: " + manager.getAllSubtasks().size());
        System.out.println("Все эпики после удаления подзадач:");
        manager.getAllEpics().forEach(System.out::println);
        System.out.println(DELIMITER);
    }

    public static void deleteAllEpicsTest(TaskManager manager) {
        System.out.println("deleteAllEpicsTest");
        manager.deleteAllEpics();
        System.out.println("Удалили все эпики. Эпиков осталось: " + manager.getAllEpics().size());
        System.out.println("Подзадач осталось: " + manager.getAllSubtasks().size());
        System.out.println(DELIMITER);
    }
}
