package managers;

public class Managers {

    public static TasksManager getDefault() {
        return FileBackedTasksManager.loadFromFile("tasks.csv");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
