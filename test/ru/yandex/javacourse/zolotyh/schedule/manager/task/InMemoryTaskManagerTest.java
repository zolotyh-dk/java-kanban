package ru.yandex.javacourse.zolotyh.schedule.manager.task;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
        super.beforeEach();
    }
}