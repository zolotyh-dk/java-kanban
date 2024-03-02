import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasks;

    public Epic(Integer id, String name, String description, Status status, List<Subtask> subtasks) {
        super(id, name, description, status);
        this.subtasks = subtasks;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", subtasksSize=" + subtasks.size() +
                '}';
    }
}
