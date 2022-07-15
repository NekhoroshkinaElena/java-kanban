package managers;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tasks.*;

public class FileBackedTasksManager extends InMemoryTasksManager {
    private final File file;

    public FileBackedTasksManager(String path) {
        this.file = new File(path);
    }

    public void save() {
        try {
            write(new FileWriter(file, false));
        } catch (IOException e) {
            throw new ManagerLoadException(e.getMessage());
        }
    }

    protected void write(Writer writer) {
        try (BufferedWriter bw = new BufferedWriter(writer)) {
            bw.write("id,type,name,status,description,startTime,duration,epic" + "\n");
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

    protected void read(Reader reader) {
        try (BufferedReader br = new BufferedReader(reader)) {
            boolean skipHeader = true;
            boolean isReadingTasks = true;
            HashMap<Integer, Task> tasks = new HashMap<>();
            int maxId = 1;
            String line;
            while ((line = br.readLine()) != null) {
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
                    if (task.getId() > maxId) {
                        maxId = task.getId();
                    }
                    tasks.put(task.getId(), task);
                    if (task.getClass() == Epic.class) {
                        this.epics.put(task.getId(), (Epic) task);
                        continue;
                    }
                    if (task.getClass() == Subtask.class) {
                        Subtask subtask = (Subtask) task;
                        this.epics.get(subtask.getEpicId()).addSubtask(subtask);
                        this.subtasks.put(task.getId(), subtask);
                        this.tasksSet.add(task);
                        continue;
                    }
                    this.tasks.put(task.getId(), task);
                    this.tasksSet.add(task);
                } else {
                    for (int id : fromString(line)) {
                        this.history.add(tasks.get(id));
                    }
                }
            }
            this.currentId = maxId;
        } catch (IOException e) {
            throw new ManagerLoadException(e.getMessage());
        }
    }

    public static FileBackedTasksManager loadFromFile(String path) {
        FileBackedTasksManager manager = new FileBackedTasksManager(path);
        if (!manager.file.exists()) {
            return manager;
        }
        try {
            manager.read(new FileReader(path));
        } catch (FileNotFoundException e) {
            throw new ManagerLoadException(e.getMessage());
        }
        return manager;
    }

    public String toString(Task task) {
        Type type = Type.getType(task);
        if (type == Type.SUBTASK) {
            return String.format("%d,%s,%s,%s,%s,%s,%d,%d", task.getId(), type, task.getName(),
                    task.getStatus(), task.getDescription(), task.getStartTime(), task.getDuration().toMinutes(),
                    ((Subtask) task).getEpicId());
        } else {
            return String.format("%d,%s,%s,%s,%s,%s,%d,", task.getId(), type, task.getName(),
                    task.getStatus(), task.getDescription(), task.getStartTime(), task.getDuration().toMinutes());
        }
    }

    public static Task taskFromString(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        Type type = Type.valueOf(split[1]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        LocalDateTime startTime = null;
        if (!split[5].equals("null")) {
            startTime = LocalDateTime.parse(split[5]);
        }
        Duration duration = Duration.ofMinutes(Integer.parseInt(split[6]));
        if (type == Type.EPIC) {
            return new Epic(id, name, description, status);
        } else if (type == Type.SUBTASK) {
            return new Subtask(id, name, description, status, Integer.parseInt(split[7]), startTime, duration);
        }
        return new Task(id, name, description, status, startTime, duration);
    }

    public static String toString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder sb = new StringBuilder();
        if (!history.isEmpty()) {
            Task last = history.get(history.size() - 1);
            for (Task task : history) {
                sb.append(task.getId());
                if (task != last) {
                    sb.append(",");
                }
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
}

