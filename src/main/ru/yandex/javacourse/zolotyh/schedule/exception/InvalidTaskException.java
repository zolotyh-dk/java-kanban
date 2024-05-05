package ru.yandex.javacourse.zolotyh.schedule.exception;

public class InvalidTaskException extends RuntimeException {
    public InvalidTaskException(String message) {
        super(message);
    }
}
