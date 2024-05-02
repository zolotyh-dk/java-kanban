package ru.yandex.javacourse.zolotyh.schedule.task;

import ru.yandex.javacourse.zolotyh.schedule.enums.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private LocalDateTime endTime;
    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(Integer id, String name, String description) {
        super(id, name, description, null);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtask(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtaskIdsSize=" + subtaskIds.size() +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}
