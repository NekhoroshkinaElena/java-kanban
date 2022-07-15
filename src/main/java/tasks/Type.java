package tasks;

public enum Type {
    TASK,
    EPIC,
    SUBTASK;

    public static Type getType(Task task) {
        if (task.getClass() == Epic.class) {
            return EPIC;
        }
        if (task.getClass() == Subtask.class) {
            return SUBTASK;
        }
        return TASK;
    }
}
