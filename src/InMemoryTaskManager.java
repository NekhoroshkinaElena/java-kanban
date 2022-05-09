import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager history = Managers.getDefaultHistory();

    int currentId = 0;

    @Override
    public int getUniqueID() {
        return currentId++;
    }

    @Override
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Epic epic, Subtask subtask) {
        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Integer ID : tasks.keySet()) {
            Task task = tasks.get(ID);
            tasksList.add(task);
        }
        return tasksList;
    }

    @Override
    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicList = new ArrayList<>();
        for (Integer ID : epics.keySet()) {
            Epic epic = epics.get(ID);
            epicList.add(epic);
        }
        return epicList;
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Integer ID : subtasks.keySet()) {
            Subtask subtask = subtasks.get(ID);
            subtaskList.add(subtask);
        }
        return subtaskList;
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtask();
        }
    }

    @Override
    public Task getTaskByID(int ID) {
        Task task = tasks.get(ID);
        history.add(task);
        return task;
    }

    @Override
    public Epic getEpicByID(int ID) {
        Epic epic = epics.get(ID);
        history.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int ID) {
        Subtask subtask = subtasks.get(ID);
        history.add(subtask);
        return subtask;
    }

    @Override
    public void removeEpic(int ID) {
        Epic epic = epics.get(ID);
        if (epic == null) {
            return;
        }
        ArrayList<Integer> subtasks = epic.getSubtaskList();
        for (Integer subtaskID : subtasks) {
            this.subtasks.remove(subtaskID);
        }
        epics.remove(ID);
    }

    @Override
    public void removeTask(int ID) {
        tasks.remove(ID);
    }

    @Override
    public void removeSubtask(int ID) {
        Subtask subtask = subtasks.get(ID);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicID());
        epic.removeSubtask(subtask);
        subtasks.remove(ID);
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasksList(Epic epic) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Integer subtaskID : epic.getSubtaskList()) {
            subtasks.add(this.subtasks.get(subtaskID));
        }
        return subtasks;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.replace(subtask.getId(), subtask) != null) {
            epics.get(subtask.getEpicID()).updateSubtask(subtask);
        }
    }
}
