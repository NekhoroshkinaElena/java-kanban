package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> map = new HashMap<>();
    private Node last;

    private void linkLast(Task task) {
        Node node = new Node(task);
        map.put(task.getId(), node);
        if (last != null) {
            last.next = node;
            node.prev = last;
        }
        last = node;
    }

    private List<Task> getTasks() {
        LinkedList<Task> list = new LinkedList<>();
        Node node = last;
        while (node != null) {
            list.addFirst(node.data);
            node = node.prev;
        }
        return new ArrayList<>(list);
    }

    public void removeNode(Node node) {
        if (node == null) {
            return;
        }
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
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = map.get(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private static class Node {
        private final Task data;
        private Node next;
        private Node prev;

        public Node(Task data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(data.getId(), node.data.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(data.getId());
        }
    }
}

