package tasks;

public class Subtask extends Task {
    private final Integer epicID;

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epicID = epic.getId();
    }

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epicID = epic.getId();
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "ID=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }

    public Integer getEpicID() {
        return epicID;
    }
}
