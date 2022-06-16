package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTasksManager implements TasksManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager history = Managers.getDefaultHistory();

    private int currentId = 1;

    private int getUniqueID() {
        return currentId++;
    }

    @Override
    public void createTask(Task task) {
        task.setId(getUniqueID());
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(getUniqueID());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Epic epic, Subtask subtask) {
        subtask.setId(getUniqueID());
        subtask.setEpicId(epic.getId());
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
        for (Integer id : epics.keySet()) {
            Epic epic = epics.get(id);
            epicList.add(epic);
        }
        return epicList;
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Integer id : subtasks.keySet()) {
            Subtask subtask = subtasks.get(id);
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
    public Task getTaskByID(int id) {
        Task task = tasks.get(id);
        history.add(task);
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = epics.get(id);
        history.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        history.add(subtask);
        return subtask;
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        ArrayList<Integer> subtasks = epic.getSubtaskList();
        for (Integer subtaskId : subtasks) {
            this.subtasks.remove(subtaskId);
            history.remove(subtaskId);
        }
        epics.remove(id);
        history.remove(id);
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(subtask);
        subtasks.remove(id);
        history.remove(id);
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasksList(Epic epic) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskList()) {
            subtasks.add(this.subtasks.get(subtaskId));
        }
        return subtasks;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public void updateTask(Task task) {
        tasks.replace(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.replace(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.replace(subtask.getId(), subtask) != null) {
            epics.get(subtask.getEpicId()).updateSubtask(subtask);
        }
    }
}
