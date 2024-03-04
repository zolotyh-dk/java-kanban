package ru.yandex.javacourse.zolotyh.schedule.task;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasks;

    public Epic(Integer id, String name, String description) {
        super(id, name, description, null);
        this.subtasks = new ArrayList<>();
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

    public boolean isAllSubtasksDone() {
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.DONE) {
                return false;
            }
        }
        return true;
    }
}
