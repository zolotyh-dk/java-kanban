package ru.yandex.javacourse.zolotyh.schedule.exception;

public class BackupLoadException extends RuntimeException {
    public BackupLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
