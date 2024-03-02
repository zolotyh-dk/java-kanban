import java.util.ArrayList;

public class Main {
    private static final String delimiter = "-".repeat(150);
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task(null,
                "Выучить инкапсуляцию.",
                "Разобраться с модификаторами доступа, геттерами и сеттерами.",
                Status.NEW);
        Task task2 = new Task(null,
                "Выучить наследование.",
                "Разобраться с ключевым словом extends и переопределением методов.",
                Status.IN_PROGRESS);

        Epic epic1 = new Epic(null,
                "Приложение канбан-доска",
                "Разработать приложение канбан-доску");
        Subtask subtask1 = new Subtask(null,
                "Разработать бэкенд",
                "Написть серверную часть приложения",
                Status.IN_PROGRESS,
                epic1);
        Subtask subtask2 = new Subtask(null,
                "Разработать фронтенд",
                "Написать клиентскую часть приложения",
                Status.NEW,
                epic1);

        Epic epic2 = new Epic(null,
                "iOS приложение",
                "Разработать iOS приложение для канбан-доски");
        Subtask subtask3 = new Subtask(null,
                "Выучить swift",
                "Без swift нам никак не написать приложение для iOS",
                Status.NEW,
                epic2);

        saveTaskTest(task1, taskManager);
        saveEpicTest(epic1, taskManager);
        saveSubtaskTest(subtask1, taskManager);

        updateTaskTest(task2, taskManager);
        updateEpicTest(epic2, taskManager);
        updateSubtaskTest(subtask3, taskManager);
    }

    private static void saveTaskTest(Task task, TaskManager manager) {
        System.out.println("saveTaskTest");
        Integer id = manager.saveOrUpdate(task).getId();
        System.out.println("Сохранено: " + manager.tasks.get(id));
        System.out.println(delimiter);
    }

    private static void saveEpicTest(Epic epic, TaskManager manager) {
        System.out.println("saveEpicTest");
        Integer id = manager.saveOrUpdate(epic).getId();
        System.out.println("Сохранено: " + manager.epics.get(id));
        System.out.println(delimiter);
    }

    private static void saveSubtaskTest(Subtask subtask, TaskManager manager) {
        System.out.println("saveSubtaskTest");
        Integer id = manager.saveOrUpdate(subtask).getId();
        System.out.println("Сохранено: " + manager.subtasks.get(id));
        System.out.println(delimiter);
    }

    private static void updateTaskTest(Task task, TaskManager manager) {
        System.out.println("updateTaskTest");
        Integer id = manager.saveOrUpdate(task).getId();
        Task updated = new Task(id,
                task.getName(),
                task.getDescription() + " А ещё с сокрытием.",
                Status.DONE);
        manager.saveOrUpdate(updated);
        System.out.println("Обновлено: " + manager.tasks.get(id));
        System.out.println(delimiter);
    }

    private static void updateEpicTest(Epic epic, TaskManager manager) {
        System.out.println("updateEpicTest");
        Integer id = manager.saveOrUpdate(epic).getId();
        Epic updated = new Epic(id, "Kanban-board iOS application ", epic.getDescription());
        manager.saveOrUpdate(updated);
        System.out.println("Обновлено: " + manager.epics.get(id));
        System.out.println(delimiter);
    }

    private static void updateSubtaskTest(Subtask subtask, TaskManager manager) {
        System.out.println("updateSubtaskTest");
        Integer id = manager.saveOrUpdate(subtask).getId();
        Subtask updated = new Subtask(id, subtask.getName(), subtask.getDescription(), Status.DONE, subtask.getEpic());
        manager.saveOrUpdate(updated);
        System.out.println("Обновлено: " + manager.subtasks.get(id));
        System.out.println(delimiter);
    }
}
