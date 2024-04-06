package ru.yandex.javacourse.zolotyh.schedule.manager;

import ru.yandex.javacourse.zolotyh.schedule.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> history = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        final int id = task.getId();
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
        linkLast(task);
        history.put(id, last);
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        final Node newNode = new Node(last, task);
        if (first == null) {
            first = newNode;
        } else {
            last.next = newNode;
        }
        last = newNode;
    }

    private List<Task> getTasks() {
        final List<Task> tasks = new ArrayList<>(history.size());
        Node node = first;
        while (node != null) {
            tasks.add(node.element);
            node = node.next;
        }
        return tasks;
    }

    private void removeNode(Node node) {
        final Node prev = node.prev;
        final Node next = node.next;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
        }
    }

    private static class Node {
        Node prev;
        Task element;
        Node next;

        Node(Node prev, Task element) {
            this.prev = prev;
            this.element = element;
        }
    }
}
