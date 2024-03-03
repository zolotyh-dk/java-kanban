package util;
import enums.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class TaskUtil {
    public static Task task1 = new Task(null,
            "Выучить инкапсуляцию.",
            "Разобраться с модификаторами доступа, геттерами и сеттерами.",
            Status.NEW);
    public static Task task2 = new Task(null,
            "Выучить наследование.",
            "Разобраться с ключевым словом extends и переопределением методов.",
            Status.DONE);

    public static Epic epic1 = new Epic(null,
            "Приложение канбан-доска",
            "Разработать приложение канбан-доску");
    public static Subtask subtask1 = new Subtask(null,
            "Разработать бэкенд",
            "Написть серверную часть приложения",
            Status.IN_PROGRESS,
            epic1);
    public static Subtask subtask2 = new Subtask(null,
            "Разработать фронтенд",
            "Написать клиентскую часть приложения",
            Status.NEW,
            epic1);

    public static Epic epic2 = new Epic(null,
            "iOS приложение",
            "Разработать iOS приложение для канбан-доски");
    public static Subtask subtask3 = new Subtask(null,
            "Выучить swift",
            "Без swift нам никак не написать приложение для iOS",
            Status.NEW,
            epic2);
}
