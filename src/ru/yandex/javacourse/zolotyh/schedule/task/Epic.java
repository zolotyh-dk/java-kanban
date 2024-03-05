package ru.yandex.javacourse.zolotyh.schedule.task;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(Integer id, String name, String description) {
        super(id, name, description, null);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", subtasksSize=" + subtaskIds.size() +
                '}';
    }
}
