package managers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class Managers {

    public static TasksManager getDefault() {
        try {
            return HTTPTaskManager.loadFromKVServer(new URL("http://localhost:8078"),
                    String.valueOf(new Random()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return FileBackedTasksManager.loadFromFile("tasks.csv");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
