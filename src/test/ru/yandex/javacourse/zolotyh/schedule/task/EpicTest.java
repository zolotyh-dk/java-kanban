package ru.yandex.javacourse.zolotyh.schedule.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    public void epicsShouldBeEqualsIfIdEquals() {
        Epic epic1 = new Epic(1, "a", "b");
        Epic epic2 = new Epic(1, "c", "d");
        assertEquals(epic1, epic2, "Эпики с одинаковыми id не равны.");
    }
}