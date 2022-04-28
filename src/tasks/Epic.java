package tasks;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private final ArrayList<Integer> subtasks = new ArrayList<>();
    private final HashMap<Integer,Status> statuses = new HashMap<>();

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public void addSubtask(Subtask subtask) {
        statuses.put(subtask.getId(), subtask.getStatus());
        subtasks.add(subtask.getId());
    }

    public ArrayList<Integer> getSubtaskList() {
        return subtasks;
    }

    public void removeSubtask(Subtask subtask) {
        statuses.remove(subtask.getId());
        subtasks.remove(subtask.getId());
    }

    public void updateSubtask(Subtask subtask) {
        for (Integer subtaskId : subtasks) {
            if (subtaskId == subtask.getId()) {
                statuses.put(subtaskId, subtask.getStatus());
                subtasks.set(subtasks.indexOf(subtaskId), subtask.getId());
                return;
            }
        }
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
