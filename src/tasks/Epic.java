package tasks;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Status> statuses = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public void addSubtask(Subtask subtask) {
        statuses.put(subtask.getId(), subtask.getStatus());
    }

    public ArrayList<Integer> getSubtaskList() {
        return new ArrayList<>(statuses.keySet());
    }

    public void removeSubtask(Subtask subtask) {
        statuses.remove(subtask.getId());
    }

    public void updateSubtask(Subtask subtask) {
        statuses.replace(subtask.getId(), subtask.getStatus());
    }

    public void clearSubtask() {
        statuses.clear();
    }

    @Override
    public Status getStatus() {
        if (statuses.isEmpty()) {
            return Status.NEW;
        }
        int newTasks = 0;
        int doneTasks = 0;
        for (Status status : statuses.values()) {
            if (status == Status.NEW) {
                newTasks++;
            }
            if (status == Status.DONE) {
                doneTasks++;
            }
        }
        if (newTasks == statuses.size()) {
            return Status.NEW;
        }
        if (doneTasks == statuses.size()) {
            return Status.DONE;
        }
        return Status.IN_PROGRESS;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "ID=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
