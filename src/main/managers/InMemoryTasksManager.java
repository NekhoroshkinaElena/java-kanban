package managers;

import java.util.*;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class InMemoryTasksManager implements TasksManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager history = Managers.getDefaultHistory();
    protected final Set<Task> tasksSet = new TreeSet<>();

    protected int currentId = 1;

    public int getCurrentId() {
        return currentId;
    }

    private int getUniqueID() {
        return currentId++;
    }

    private boolean isIntersect(Task task) {
        for (Task t : tasksSet) {
            if (t.isIntersect(task)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void createTask(Task task) {
        if (task == null || isIntersect(task)) {
            return;
        }
        task.setId(getUniqueID());
        tasks.put(task.getId(), task);
        tasksSet.add(task);
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        epic.setId(getUniqueID());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Epic epic, Subtask subtask) {
        if (epic == null || subtask == null || isIntersect(subtask)) {
            return;
        }
        epic = epics.get(epic.getId());
        if (epic == null) {
            return;
        }

        subtask.setId(getUniqueID());
        subtask.setEpicId(epic.getId());
        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        tasksSet.add(subtask);
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
        tasks.forEach((taskId, task) -> tasksSet.remove(task));
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.forEach((epicId, epic) -> tasksSet.remove(epic));
        subtasks.forEach((subtaskId, subtask) -> tasksSet.remove(subtask));
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void clearSubtasks() {
        subtasks.forEach((subtaskId, subtask) -> tasksSet.remove(subtask));
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
        tasksSet.remove(epic);
    }

    @Override
    public void removeTask(int id) {
        Task task = getTaskByID(id);
        if (task == null) {
            return;
        }
        tasksSet.remove(task);
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
        tasksSet.remove(subtask);
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
        if(task == null){
            return;
        }
        Task oldTask = tasks.get(task.getId());
        if (oldTask == null) {
            return;
        }
        tasks.remove(oldTask.getId());
        tasksSet.remove(oldTask);
        if (isIntersect(task)) {
            tasks.put(oldTask.getId(), oldTask);
            tasksSet.add(oldTask);
            return;
        }
        tasks.put(task.getId(), task);
        tasksSet.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        epics.replace(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if(subtask == null){
            return;
        }
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if(oldSubtask == null){
            return;
        }
        subtasks.remove(oldSubtask.getId());
        tasksSet.remove(oldSubtask);
        if (isIntersect(subtask)) {
            subtasks.put(oldSubtask.getId(), oldSubtask);
            tasksSet.add(oldSubtask);
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).updateSubtask(subtask);
        tasksSet.add(subtask);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(tasksSet);
    }
}
