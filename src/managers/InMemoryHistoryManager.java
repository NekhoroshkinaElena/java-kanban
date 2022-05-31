package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> map = new HashMap<>();
    private Node last;

    public void linkLast(Task task) {
        Node node = new Node(task);
        map.put(task.getId(), node);
        if (last != null) {
            last.next = node;
            node.prev = last;
        }
        last = node;
    }

    public List<Task> getTasks() {
        LinkedList<Task> list = new LinkedList<>();
        Node node = last;
        while (node != null) {
            list.addFirst(node.data);
            node = node.prev;
        }
        return new ArrayList<>(list);
    }

    public void removeNode(Node node) {
        map.remove(node.data.getId());
        if (last.equals(node)) {
            last = last.prev;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        }
    }


    @Override
    public void add(Task task) {
        Node node = map.get(task.getId());
        if (node != null) {
            removeNode(node);
        }
        linkLast(task);
    }

    @Override//переопределён метод интерфейса
    public void remove(int id) {
        Node node = map.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
