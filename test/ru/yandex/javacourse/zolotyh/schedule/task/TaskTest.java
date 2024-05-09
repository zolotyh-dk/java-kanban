package ru.yandex.javacourse.zolotyh.schedule.task;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.zolotyh.schedule.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void tasksShouldBeEqualsIfIdEquals() {
        final Task task1 = new Task(1, "a", "b", Status.NEW);
        final Task task2 = new Task(1, "c", "d", Status.DONE);
        assertEquals(task1, task2, "Задачи с одинаковыми id не равны.");
    }

    @Test
    public void endTimeCalculation() {
        final Task task = new Task(1, "Задача", "Описание задачи", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2023, Month.DECEMBER, 31, 23, 59));
        assertEquals(LocalDateTime.of(2024, Month.JANUARY, 1, 0, 29), task.getEndTime(),
                "Время окончания выполнения задачи не соответствует.");
    }

    @Test
    public void endTimeShouldBeNullWhenStartOrDurationIsNull() {
        final Task task = new Task(1, "Задача", "Описание задачи", Status.NEW);
        assertNull(task.getEndTime());

        task.setDuration(Duration.ofMinutes(30));
        assertNull(task.getEndTime());

        task.setStartTime(LocalDateTime.of(2023, Month.DECEMBER, 31, 23, 59));
        task.setDuration(null);
        assertNull(task.getEndTime());
    }
}