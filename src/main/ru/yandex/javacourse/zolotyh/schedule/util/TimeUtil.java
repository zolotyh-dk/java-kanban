package ru.yandex.javacourse.zolotyh.schedule.util;

import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeUtil {
    // Проверка методом наложения отрезков
    public static boolean isTimeIntersection(Task newTask, List<Task> existedTasks) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false;
        }
        return existedTasks.stream().anyMatch(existedTask -> TimeUtil.isTimeIntersection(newTask, existedTask));
    }

    public static boolean isTimeIntersection(Task newTask, Task existedTask) {
        return newTask.getStartTime().isBefore(existedTask.getEndTime()) &&
                newTask.getEndTime().isAfter(existedTask.getStartTime());
    }

    // Проверка через HashMap
    public static boolean isTimeIntersection(Task newTask, TimeSlotTable timeSlotTable) {
        return timeSlotTable.isTimeIntervalBusy(newTask.getStartTime(), newTask.getEndTime());
    }

    public static class TimeSlotTable {
        private final Map<LocalDateTime, Boolean> slots;

        public TimeSlotTable() {
            // Создание таблицы на год вперед
            slots = new HashMap<>();
            LocalDateTime current = LocalDateTime.of(2024, Month.MAY, 1, 0, 0);
            LocalDateTime endOfYear = current.plusYears(1);
            while (current.isBefore(endOfYear)) {
                slots.put(current, false); // Изначально все временные слоты свободны
                current = current.plusMinutes(15);
            }
        }

        public boolean isTimeIntervalBusy(LocalDateTime start, LocalDateTime end) {
            if (start == null || end == null) {
                return false;
            }
            LocalDateTime current = start;
            while (current.isBefore(end)) {
                if (!slots.containsKey(current) || slots.get(current)) {
                    System.out.println(slots.get(current));
                    return true;
                }
                current = current.plusMinutes(15);
            }
            return false;
        }

        public void bookTimeInterval(LocalDateTime start, LocalDateTime end) {
            LocalDateTime current = start;
            while (current.isBefore(end)) {
                slots.put(current, true);
                current = current.plusMinutes(15);
            }
        }

        public void freeTimeInterval(LocalDateTime start, LocalDateTime end) {
            if (start == null || end == null) {
                return;
            }
            LocalDateTime current = start;
            while (current.isBefore(end)) {
                slots.put(current, false);
                current = current.plusMinutes(15);
            }
        }
    }
}