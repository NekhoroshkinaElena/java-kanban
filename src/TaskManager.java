import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    void clearTasks();

    void clearEpics();

    void clearSubtasks();

    Task getTaskByID(int ID);

    Epic getEpicByID(int ID);

    Subtask getSubtaskByID(int ID);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Epic epic, Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeEpic(int ID);

    void removeTask(int ID);

    void removeSubtask(int ID);

    ArrayList<Subtask> getEpicSubtasksList(Epic epic);

    List<Task> getHistory();
}

