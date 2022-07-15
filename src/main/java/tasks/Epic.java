package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description, null, Duration.ZERO);
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status, null, Duration.ZERO);
    }

    public void addSubtask(Subtask subtask) {
        this.subtasks.put(subtask.getId(), subtask);
    }

    public ArrayList<Integer> getSubtaskList() {
        return new ArrayList<>(subtasks.keySet());
    }

    public void removeSubtask(Subtask subtask) {
        this.subtasks.remove(subtask.getId());
    }

    public void updateSubtask(Subtask subtask) {
        this.subtasks.replace(subtask.getId(), subtask);
    }

    public void clearSubtask() {
        subtasks.clear();
    }

    @Override
    public Status getStatus() {
        if (subtasks.isEmpty()) {
            return Status.NEW;
        }
        int newTasks = 0;
        int doneTasks = 0;
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStatus() == Status.NEW) {
                newTasks++;
            }
            if (subtask.getStatus() == Status.DONE) {
                doneTasks++;
            }
        }
        if (newTasks == subtasks.size()) {
            return Status.NEW;
        }
        if (doneTasks == subtasks.size()) {
            return Status.DONE;
        }
        return Status.IN_PROGRESS;
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subtasks.isEmpty()) {
            return null;
        }
        LocalDateTime dateTime = LocalDateTime.MAX;
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStartTime().isBefore(dateTime)) {
                dateTime = subtask.getStartTime();
            }
        }
        return dateTime;
    }

    @Override
    public Duration getDuration() {

        if (subtasks.isEmpty()) {
            return Duration.ZERO;
        }
        Duration duration = Duration.ZERO;
        for (Subtask subtask : subtasks.values()) {
            duration = duration.plus(subtask.getDuration());
        }
        return duration;
    }

    public LocalDateTime getEndTime() {
        return getStartTime().plus(getDuration());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "ID=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration().toMinutes() +
                ", startTime=" + getStartTime() +
                '}';
    }
}
