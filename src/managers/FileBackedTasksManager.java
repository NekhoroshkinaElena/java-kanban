package managers;

import tasks.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTasksManager {
    private final File file;

    public FileBackedTasksManager(String path) {
        this.file = new File(path);
    }

    public void save() {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))){
            bw.write("id,type,name,status,description,epic" + "\n");
            for (Task task : getTasks()) {
                bw.write(toString(task) + "\n");
            }
            // Записываем эпики перед подзадачами, чтобы при чтении заполнять подзадачи в уже прочитанных эпиках
            for (Epic epic : getEpics()) {
                bw.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                bw.write(toString(subtask) + "\n");
            }
            bw.write("\n");
            bw.write(toString(history));
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public static FileBackedTasksManager loadFromFile(String path) {
        FileBackedTasksManager manager = new FileBackedTasksManager(path);
        if (!manager.file.exists()) {
            return manager;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            boolean skipHeader = true;
            boolean isReadingTasks = true;
            HashMap<Integer, Task> tasks = new HashMap<>();
            while (br.ready()) {
                String line = br.readLine();
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }
                if (line.isEmpty()) {
                    isReadingTasks = false;
                    continue;
                }
                if (isReadingTasks) {
                    Task task = taskFromString(line);
                    tasks.put(task.getId(), task);
                    if (task.getClass() == Epic.class) {
                        manager.epics.put(task.getId(), (Epic) task);
                        continue;
                    }
                    if (task.getClass() == Subtask.class) {
                        Subtask subtask = (Subtask) task;
                        manager.epics.get(subtask.getEpicId()).addSubtask(subtask);
                        manager.subtasks.put(task.getId(), subtask);
                        continue;
                    }
                    manager.tasks.put(task.getId(), task);
                } else {
                    for (int id : fromString(line)) {
                        manager.history.add(tasks.get(id));
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException(e.getMessage());
        }
        return manager;
    }

    public String toString(Task task) {
        Type type = Type.getType(task);
        if (type == Type.SUBTASK) {
            return String.format("%d,%s,%s,%s,%s,%d", task.getId(), Type.getType(task), task.getName(),
                    task.getStatus(), task.getDescription(), ((Subtask) task).getEpicId());
        } else {
            return String.format("%d,%s,%s,%s,%s,", task.getId(), Type.getType(task), task.getName(),
                    task.getStatus(), task.getDescription());
        }
    }

    public static Task taskFromString(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        Type type = Type.valueOf(split[1]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        if (type == Type.EPIC) {
            return new Epic(id, name, description, status);
        } else if (type == Type.SUBTASK) {
            return new Subtask(id, name, description, status, Integer.parseInt(split[5]));
        }
        return new Task(id, name, description, status);
    }

    public static String toString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        Task last = history.get(history.size() - 1);
        StringBuilder sb = new StringBuilder();
        for (Task task : history) {
            sb.append(task.getId());
            if(task != last){
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static List<Integer> fromString(String value) {
        List<Integer> ids = new ArrayList<>();
        String[] taskId = value.split(",");
        for (String i : taskId) {
            ids.add(Integer.parseInt(i));
        }
        return ids;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Epic epic, Subtask subtask) {
        super.createSubtask(epic, subtask);
        save();
    }

    @Override
    public Task getTaskByID(int id) {
        Task task = super.getTaskByID(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = super.getEpicByID(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = super.getSubtaskByID(id);
        save();
        return subtask;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    // По ТЗ создаем дополнительный метод main для проверки FileBackedTasksManager
    public static void main(String[] args) {
        TasksManager manager = Managers.getDefault();

        Task task1 = new Task("task1", "description task1");
        Task task2 = new Task("task2", "description task2");
        Epic epic1 = new Epic("epic1", "description epic1");
        Subtask subtask1 = new Subtask("subtask1", "description subtask1", epic1);
        Subtask subtask2 = new Subtask("subtask2", "description subtask2", epic1);

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(epic1, subtask1);
        manager.createSubtask(epic1, subtask2);

        System.out.println(manager.getTaskByID(task1.getId()));
        System.out.println(manager.getTaskByID(task2.getId()));
        System.out.println(manager.getEpicByID(epic1.getId()));
        System.out.println(manager.getSubtaskByID(subtask1.getId()));
        System.out.println(manager.getSubtaskByID(subtask2.getId()));
        System.out.println();

        System.out.println(manager.getHistory());
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getEpicSubtasksList(epic1));
        System.out.println(manager.getSubtasks());
        System.out.println();

        TasksManager manager2 = Managers.getDefault();
        System.out.println(manager2.getHistory());
        System.out.println(manager2.getTasks());
        System.out.println(manager2.getEpics());
        System.out.println(manager2.getEpicSubtasksList(epic1));
        System.out.println(manager2.getSubtasks());
    }
}

