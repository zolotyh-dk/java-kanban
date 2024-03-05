package ru.yandex.javacourse.zolotyh.schedule.task;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(Integer id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", epicId=" + epicId +
                '}';
    }
}
