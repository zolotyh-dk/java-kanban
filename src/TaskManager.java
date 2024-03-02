import java.util.HashMap;

public class TaskManager {
    private int nextId = 0;
    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Task saveOrUpdate(Task task) {
        System.out.println("Метод saveOrUpdate(Task task)");
        System.out.println(task);

        if (task.getId() == null) {
            task.setId(nextId++);
        }
        tasks.put(task.getId(), task);
        return task;
    }

    public Subtask saveOrUpdate(Subtask subtask) {
        System.out.println("Метод SaveOrUpdate(Subtask subtask)");
        System.out.println(subtask);

        if (subtask.getId() == null) {
            subtask.setId(nextId++);
        }
        subtasks.put(subtask.getId(), subtask);
        return subtask;
    }

    public Epic saveOrUpdate(Epic epic) {
        System.out.println("Метод saveOrUpdate(Epic epic)");
        System.out.println(epic);

        if (epic.getId() == null) {
            epic.setId(nextId++);
        }
        epics.put(epic.getId(), epic);
        return epic;
    }


}
