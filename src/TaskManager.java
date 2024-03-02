import java.util.HashMap;

public class TaskManager {
    private int nextId = 0;
    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Task saveOrUpdate(Task task) {
        System.out.println("Метод saveOrUpdate(Task task)");
        System.out.println("В параметрах " + task);

        if (task.getId() == null) {
            task.setId(nextId++);
        }
        tasks.put(task.getId(), task);
        return task;
    }

    public Subtask saveOrUpdate(Subtask subtask) {
        System.out.println("Метод SaveOrUpdate(Subtask subtask)");
        System.out.println("В параметрах " + subtask);

        if (subtask.getId() == null) {
            subtask.setId(nextId++);
        }
        subtasks.put(subtask.getId(), subtask);

        Epic epic = subtask.getEpic();
        int index = epic.getSubtasks().indexOf(subtask);
        if (index == -1) {
            epic.getSubtasks().add(subtask);
        } else {
            epic.getSubtasks().set(index, subtask);
        }
        saveOrUpdate(epic);
        return subtask;
    }

    public Epic saveOrUpdate(Epic epic) {
        System.out.println("Метод saveOrUpdate(Epic epic)");
        System.out.println("В параметрах " + epic);

        if (epic.getId() == null) {
            epic.setId(nextId++);
        }

        if (epic.getSubtasks() == null || epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            System.out.println("Присвоен статус " + Status.NEW);
        } else if (epic.isAllSubtasksDone()) {
            epic.setStatus(Status.DONE);
            System.out.println("Присвоен статус " + Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
            System.out.println("Присвоен статус " + Status.IN_PROGRESS);
        }

        epics.put(epic.getId(), epic);
        return epic;
    }


}
