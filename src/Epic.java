import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public ArrayList<Subtask> getSubtaskList() {
        return subtasks;
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public void updateSubtask(Subtask subtask) {
        Subtask target = null;
        for (Subtask s : subtasks) {
            if (s.getId() == subtask.getId()) {
                target = s;
                break;
            }
        }
        if (target != null) {
            subtasks.set(subtasks.indexOf(target), subtask);
        }
    }

    @Override
    public Status getStatus() {
        if (subtasks.isEmpty()) {
            return Status.NEW;
        }
        int newTasks = 0;
        int doneTasks = 0;
        for (Subtask subtask : subtasks) {
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
    public String toString() {
        return "Epic{" +
                "ID=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
