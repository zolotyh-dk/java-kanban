package ru.yandex.javacourse.zolotyh.schedule.task;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.zolotyh.schedule.enums.Status;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void tasksShouldBeEqualsIfIdEquals() {
        final Task task1 = new Task(1, "a", "b", Status.NEW);
        final Task task2 = new Task(1, "c", "d", Status.DONE);
        assertEquals(task1, task2, "Задачи с одинаковыми id не равны.");
    }
}