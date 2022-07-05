package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    private int id;
    private final String name;
    private final String description;
    private final Status status;
    private final Duration duration;
    private final LocalDateTime startTime;

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this(name, description, Status.NEW, startTime, duration);
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this(0, name, description, status, startTime, duration);
    }

    public Task(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = (duration == null) ? Duration.ZERO : duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public int compareTo(Task task) {
        LocalDateTime thisStartTime = (this.getStartTime() == null) ? LocalDateTime.MAX : this.getStartTime();
        LocalDateTime taskStartTime = (task.getStartTime() == null) ? LocalDateTime.MAX : task.getStartTime();
        return thisStartTime.compareTo(taskStartTime);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public boolean isIntersect(Task task) {
        if (task == null || task.getStartTime() == null || this.getStartTime() == null) {
            return false;
        }
        return !this.getEndTime().isBefore(task.getStartTime()) && !this.getStartTime().isAfter(task.getEndTime());
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration.toMinutes() +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}





