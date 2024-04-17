package ru.yandex.javacourse.zolotyh.schedule.task;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.zolotyh.schedule.enums.Status;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    public void subtaskShouldBeEqualsIfIdEquals() {
        Subtask subtask1 = new Subtask(1, "a", "b", Status.NEW, 2);
        Subtask subtask2 = new Subtask(1, "c", "d", Status.DONE, 3);
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковыми id не равны.");
    }

}