import java.util.ArrayList;

public class Main {
    private static final String delimiter = "-".repeat(70);
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task(null,
                "Написать метод saveOrUpdate",
                "Метод должен сохранять новую задачу или обновлять существующую",
                Status.NEW);
        Task task2 = new Task(100_000,
                "Протестировать метод saveOrUpdate",
                "Проверить что задачи корректно сохраняются и удаляются",
                Status.IN_PROGRESS);
        Epic epic1 = new Epic(null,
                "Приложение канбан-доска",
                "Разработать приложение канбан-доску",
                Status.IN_PROGRESS,
                new ArrayList<>());
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
                "Разработать iOS приложение для канбан-доски",
                Status.NEW,
                new ArrayList<>());
        Subtask subtask3 = new Subtask(null,
                "Выучить swift",
                "Без swift нам никак не написать приложение для iOS",
                Status.NEW,
                epic2);

        saveTaskTest(task1, taskManager);
        saveEpicTest(epic1, taskManager);
        saveSubtaskTest(subtask1, taskManager);
    }

    private static void saveTaskTest(Task task, TaskManager manager) {
        Integer id = manager.saveOrUpdate(task).id;
        System.out.println(manager.tasks.get(id));
        System.out.println(delimiter);
    }

    private static void saveEpicTest(Epic epic, TaskManager manager) {
        Integer id = manager.saveOrUpdate(epic).id;
        System.out.println(manager.epics.get(id));
        System.out.println(delimiter);
    }

    private static void saveSubtaskTest(Subtask subtask, TaskManager manager) {
        Integer id = manager.saveOrUpdate(subtask).id;
        System.out.println(manager.subtasks.get(id));
        System.out.println(delimiter);
    }
}
