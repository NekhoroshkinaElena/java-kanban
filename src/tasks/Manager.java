package tasks;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    int currentId = 0;

    public int getUniqueID() {
        return currentId++;
    }

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Epic epic, Subtask subtask) {
        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Integer ID : tasks.keySet()) {
            Task task = tasks.get(ID);
            tasksList.add(task);
        }
        return tasksList;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicList = new ArrayList<>();
        for (Integer ID : epics.keySet()) {
            Epic epic = epics.get(ID);
            epicList.add(epic);
        }
        return epicList;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Integer ID : subtasks.keySet()) {
            Subtask subtask = subtasks.get(ID);
            subtaskList.add(subtask);
        }
        return subtaskList;
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public Task getTaskByID(int ID) {
        return tasks.get(ID);
    }

    public Epic getEpicByID(int ID) {
        return epics.get(ID);
    }

    public Subtask getSubtaskByID(int ID) {
        return subtasks.get(ID);
    }

    public void removeEpic(int ID) {
        Epic epic = getEpicByID(ID);
        if (epic == null) {
            return;
        }
        ArrayList<Subtask> subtasks = epic.getSubtaskList();
        for (Subtask subtask : subtasks) {
            this.subtasks.remove(subtask.getId());
        }
        epics.remove(ID);
    }

    public void removeTask(int ID) {
        tasks.remove(ID);
    }

    public void removeSubtask(int ID) {
        Subtask subtask = getSubtaskByID(ID);
        if (subtask == null) {
            return;
        }
        Epic epic = subtask.getEpic();
        epic.removeSubtask(subtask);
        subtasks.remove(ID);
    }

    public ArrayList<Subtask> getEpicSubtasksList(Epic epic) {
        return epic.getSubtaskList();
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtask.getEpic().updateSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
    }
}