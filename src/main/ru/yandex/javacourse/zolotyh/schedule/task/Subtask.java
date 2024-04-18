package ru.yandex.javacourse.zolotyh.schedule.task;

import ru.yandex.javacourse.zolotyh.schedule.enums.Status;
import ru.yandex.javacourse.zolotyh.schedule.enums.TaskType;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(Integer id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        final String format = "%d,%s,%s,%s,%s,%d";
        return String.format(format, getId(), TaskType.SUBTASK, getName(), getStatus(), getDescription(), epicId);
    }
}
