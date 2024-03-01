import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int idSequence = 0;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
}
