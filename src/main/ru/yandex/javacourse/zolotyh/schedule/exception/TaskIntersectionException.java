package ru.yandex.javacourse.zolotyh.schedule.exception;

public class TaskIntersectionException extends RuntimeException {
    public TaskIntersectionException(String message) {
        super(message);
    }
}
