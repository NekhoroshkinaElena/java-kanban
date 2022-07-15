import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import managers.FileBackedTasksManager;
import managers.TasksManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TasksManagerTest<FileBackedTasksManager> {

    @TempDir
    Path tempDir;

    @Override
    FileBackedTasksManager getTaskManager() {
        return new FileBackedTasksManager(tempDir.resolve("test.csv").toString());
    }

    @Test
    public void tasksIsEmpty() {
        assertTrue(tasksManager.getTasks().isEmpty());
        assertTrue(tasksManager.getSubtasks().isEmpty());
        assertTrue(tasksManager.getEpics().isEmpty());

        Task task = new Task("T", "S",
                LocalDateTime.of(2022, 7, 13, 0, 0, 0),
                Duration.ofMinutes(60));
        Epic epic = new Epic("E", "D");
        Subtask subtask = new Subtask("S", "D", epic,
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createTask(task);
        tasksManager.createEpic(epic);
        tasksManager.createSubtask(epic, subtask);

        assertEquals(List.of(task), tasksManager.getTasks());
        assertEquals(List.of(epic), tasksManager.getEpics());
        assertEquals(List.of(subtask), tasksManager.getSubtasks());
    }

    @Test
    public void epicWithoutSubtask() {
        Epic epic = new Epic("e", "d");
        tasksManager.createEpic(epic);
        assertTrue(tasksManager.getEpicSubtasksList(epic).isEmpty());

        Subtask subtask = new Subtask("S", "D", epic,
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createSubtask(epic, subtask);
        assertEquals(List.of(subtask), tasksManager.getEpicSubtasksList(epic));
    }

    @Test
    public void historyIsEmpty() {
        assertTrue(tasksManager.getHistory().isEmpty());

        Task task = new Task("T", "D",
                LocalDateTime.of(2022, 7, 13, 0, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createTask(task);
        tasksManager.getTaskByID(task.getId());

        assertEquals(List.of(task), tasksManager.getHistory());
    }

    @Test
    public void loadFromFile() {
        Task task = new Task("T", "D",
                LocalDateTime.of(2022, 7, 13, 0, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createTask(task);

        Epic epic = new Epic("e", "d");
        tasksManager.createEpic(epic);

        Subtask subtask = new Subtask("S", "D", epic,
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createSubtask(epic, subtask);

        FileBackedTasksManager loadedFileManager =
                FileBackedTasksManager.loadFromFile(tempDir.resolve("test.csv").toString());

        assertEquals(List.of(task), loadedFileManager.getTasks());
        assertEquals(List.of(epic), loadedFileManager.getEpics());
        assertEquals(List.of(subtask), loadedFileManager.getSubtasks());

        assertEquals(List.of(task, subtask), loadedFileManager.getPrioritizedTasks());

        assertEquals(3, loadedFileManager.getCurrentId());
    }
}