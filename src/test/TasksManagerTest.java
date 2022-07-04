import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import managers.TasksManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;


abstract class TasksManagerTest<T extends TasksManager> {

    protected T tasksManager;

    abstract T getTaskManager();

    @BeforeEach
    public void beforeEach() {
        tasksManager = getTaskManager();
    }

    @Test
    public void getTasks() {
        assertTrue(tasksManager.getTasks().isEmpty());

        Task task = new Task("Task", "Description",
                LocalDateTime.of(2022, 7, 13, 14, 0, 0),
                Duration.ofMinutes(60));
        Task task2 = new Task("Task 2", "Description 2",
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createTask(task);
        tasksManager.createTask(task2);
        assertEquals(Arrays.asList(task, task2), tasksManager.getTasks());
    }

    @Test
    public void getEpics() {
        assertTrue(tasksManager.getEpics().isEmpty());

        Epic epic = new Epic("E", "D");
        tasksManager.createEpic(epic);
        Epic epic2 = new Epic("E2", "D2");
        tasksManager.createEpic(epic2);
        Epic epic3 = new Epic("E3", "D3");
        tasksManager.createEpic(epic3);
        assertEquals(List.of(epic, epic2, epic3), tasksManager.getEpics());
    }

    @Test
    public void getSubtasks() {
        assertTrue(tasksManager.getSubtasks().isEmpty());

        Epic epic = new Epic("E", "D");
        tasksManager.createEpic(epic);
        Subtask subtask = new Subtask("E", "D", epic,
                LocalDateTime.of(2022, 7, 13, 12, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createSubtask(epic, subtask);
        Subtask subtask2 = new Subtask("E2", "D2", epic,
                LocalDateTime.of(2022, 7, 13, 14, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createSubtask(epic, subtask2);
        Subtask subtask3 = new Subtask("E3", "D3", epic,
                LocalDateTime.of(2022, 7, 13, 16, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createSubtask(epic, subtask3);
        assertEquals(List.of(subtask, subtask2, subtask3), tasksManager.getSubtasks());
    }

    @Test
    public void clearTasks() {
        tasksManager.clearTasks();
        assertTrue(tasksManager.getTasks().isEmpty());

        Task task = new Task("T", "d",
                LocalDateTime.of(2022, 7, 13, 0, 0, 0),
                Duration.ofMinutes(60));
        Task task2 = new Task("T2", "d2",
                LocalDateTime.of(2022, 7, 13, 0, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createTask(task);
        tasksManager.createTask(task2);
        tasksManager.clearTasks();
        assertTrue(tasksManager.getTasks().isEmpty());
    }

    @Test
    public void clearEpics() {
        tasksManager.clearEpics();
        assertTrue(tasksManager.getEpics().isEmpty());
        assertTrue(tasksManager.getSubtasks().isEmpty());

        Epic epic = new Epic("e", "d");
        Subtask subtask = new Subtask("s", "d", epic,
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        Subtask subtask2 = new Subtask("s2", "d2", epic,
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createEpic(epic);
        tasksManager.createSubtask(epic, subtask);
        tasksManager.createSubtask(epic, subtask2);
        tasksManager.clearEpics();

        assertTrue(tasksManager.getEpics().isEmpty());
        assertTrue(tasksManager.getSubtasks().isEmpty());
    }

    @Test
    public void clearSubtask() {
        tasksManager.clearSubtasks();
        assertTrue(tasksManager.getSubtasks().isEmpty());

        Epic epic = new Epic("E", "D");
        tasksManager.createEpic(epic);
        Subtask subtask = new Subtask("E", "D", epic,
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createSubtask(epic, subtask);
        Subtask subtask2 = new Subtask("E2", "D2", epic,
                LocalDateTime.of(2022, 7, 11, 11, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createSubtask(epic, subtask2);
        assertEquals(Arrays.asList(subtask.getId(), subtask2.getId()), epic.getSubtaskList());

        tasksManager.clearSubtasks();
        assertTrue(tasksManager.getSubtasks().isEmpty());
        assertTrue(epic.getSubtaskList().isEmpty());
    }

    @Test
    public void getTaskByID() {
        Task task = new Task("T", "D",
                LocalDateTime.of(2022, 7, 13, 0, 0, 0),
                Duration.ofMinutes(60));
        assertNull(tasksManager.getTaskByID(task.getId()));

        tasksManager.createTask(task);
        assertEquals(task, tasksManager.getTaskByID(task.getId()));

        assertNull(tasksManager.getTaskByID(-8));
    }

    @Test
    public void getEpicByID() {
        Epic epic = new Epic("E", "d");
        assertNull(tasksManager.getEpicByID(epic.getId()));

        tasksManager.createEpic(epic);
        assertEquals(epic, tasksManager.getEpicByID(epic.getId()));

        assertNull(tasksManager.getEpicByID(-158));
    }

    @Test
    public void getSubtaskById() {
        Epic epic = new Epic("E", "d");
        tasksManager.createEpic(epic);
        Subtask subtask = new Subtask("S", "d", epic,
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        assertNull(tasksManager.getSubtaskByID(subtask.getId()));

        tasksManager.createSubtask(epic, subtask);
        assertEquals(subtask, tasksManager.getSubtaskByID(subtask.getId()));
    }

    @Test
    public void createTask() {
        assertDoesNotThrow(() -> tasksManager.createTask(null));
        assertTrue(tasksManager.getTasks().isEmpty());

        Task task = new Task("T", "D",
                LocalDateTime.of(2022, 7, 13, 0, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createTask(task);
        assertEquals(task, tasksManager.getTaskByID(task.getId()));
    }

    @Test
    public void createEpic() {
        assertDoesNotThrow(() -> tasksManager.createEpic(null));
        assertTrue(tasksManager.getEpics().isEmpty());

        Epic epic = new Epic("E", "D");
        tasksManager.createEpic(epic);
        assertEquals(epic, tasksManager.getEpicByID(epic.getId()));
    }

    @Test
    public void createSubtask() {
        assertDoesNotThrow(() -> tasksManager.createSubtask(null, null));
        assertTrue(tasksManager.getSubtasks().isEmpty());

        Epic epic = new Epic("E", "D");
        assertDoesNotThrow(() -> tasksManager.createSubtask(epic, null));
        assertTrue(tasksManager.getSubtasks().isEmpty());

        Subtask subtask = new Subtask("S", "D", epic,
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        assertDoesNotThrow(() -> tasksManager.createSubtask(epic, subtask));
        assertTrue(tasksManager.getSubtasks().isEmpty());

        tasksManager.createEpic(epic);
        assertDoesNotThrow(() -> tasksManager.createSubtask(epic, null));
        assertTrue(tasksManager.getSubtasks().isEmpty());

        tasksManager.createSubtask(epic, subtask);
        assertEquals(subtask, tasksManager.getSubtaskByID(subtask.getId()));
    }

    @Test
    public void updateTask() {
        Task task = new Task("T", "D",
                LocalDateTime.of(2022, 7, 13, 0, 0, 0),
                Duration.ofMinutes(60));
        Task taskUpdate = new Task(task.getId(), "T U", "D U", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 7, 13, 0, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.updateTask(taskUpdate);
        assertTrue(tasksManager.getTasks().isEmpty());

        tasksManager.createTask(task);
        taskUpdate.setId(task.getId());
        tasksManager.updateTask(taskUpdate);
        assertEquals(taskUpdate, tasksManager.getTaskByID(task.getId()));
    }

    @Test
    public void updateEpic() {
        Epic epic = new Epic("T", "D");
        Epic epicUpdate = new Epic(epic.getId(), "E U", "D U", Status.IN_PROGRESS);
        tasksManager.updateEpic(epicUpdate);
        assertTrue(tasksManager.getTasks().isEmpty());

        tasksManager.createEpic(epic);
        epicUpdate.setId(epic.getId());
        tasksManager.updateEpic(epicUpdate);
        assertEquals(epicUpdate, tasksManager.getEpicByID(epic.getId()));
    }

    @Test
    public void updateSubtask() {
        Epic epic = new Epic("T", "D");
        tasksManager.createEpic(epic);
        Subtask subtask = new Subtask("S", "D", epic,
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createSubtask(epic, subtask);

        Subtask subtaskUpdate = new Subtask(subtask.getId(), "S U", "D U", Status.IN_PROGRESS,
                epic.getId(), LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.updateSubtask(subtaskUpdate);
        assertEquals(subtaskUpdate, tasksManager.getSubtaskByID(subtask.getId()));
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
        assertEquals(List.of(subtaskUpdate), tasksManager.getPrioritizedTasks());
        assertEquals(List.of(subtaskUpdate.getId()),epic.getSubtaskList());
    }

    @Test
    public void removeEpic() {
        Epic epic = new Epic("T", "D");
        Subtask subtask = new Subtask("S", "D", epic,
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createEpic(epic);
        assertTrue(tasksManager.getEpicSubtasksList(epic).isEmpty());

        tasksManager.createSubtask(epic, subtask);
        assertFalse(tasksManager.getEpicSubtasksList(epic).isEmpty());
        assertEquals(epic, tasksManager.getEpicByID(epic.getId()));
        assertEquals(List.of(subtask), tasksManager.getEpicSubtasksList(epic));

        tasksManager.removeEpic(epic.getId());

        assertNull(tasksManager.getEpicByID(epic.getId()));
        assertEquals("[null]", tasksManager.getEpicSubtasksList(epic).toString());
    }

    @Test
    public void removeTask() {
        Task task = new Task("T", "D",
                LocalDateTime.of(2022, 7, 13, 0, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.removeTask(task.getId());
        assertNull(tasksManager.getTaskByID(task.getId()));


        tasksManager.createTask(task);
        tasksManager.removeTask(task.getId());
        assertNull(tasksManager.getTaskByID(task.getId()));
    }

    @Test
    public void removeSubtask() {
        Epic epic = new Epic("T", "D");
        Subtask subtask = new Subtask("S", "D", epic,
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        Subtask subtask2 = new Subtask("S 2", "D 2", epic,
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createEpic(epic);
        tasksManager.createSubtask(epic, subtask);
        tasksManager.createSubtask(epic, subtask2);

        tasksManager.removeSubtask(subtask.getId());
        tasksManager.removeSubtask(subtask2.getId());

        assertNull(tasksManager.getSubtaskByID(subtask.getId()));
        assertTrue(tasksManager.getEpicSubtasksList(epic).isEmpty());
    }

    @Test
    public void getEpicSubtasksList() {
        Epic epic = new Epic("E", "D");
        tasksManager.createEpic(epic);
        assertTrue(tasksManager.getEpicSubtasksList(epic).isEmpty());

        Subtask subtask = new Subtask("S", "D", epic,
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        Subtask subtask2 = new Subtask("S 2", "D 2", epic,
                LocalDateTime.of(2022, 7, 13, 12, 0, 1),
                Duration.ofMinutes(60));
        tasksManager.createSubtask(epic, subtask);
        tasksManager.createSubtask(epic, subtask2);

        assertEquals(Arrays.asList(subtask, subtask2), tasksManager.getEpicSubtasksList(epic));
    }

    @Test
    public void getHistory() {
        assertTrue(tasksManager.getHistory().isEmpty());

        Task task = new Task("T", "D",
                LocalDateTime.of(2022, 7, 13, 0, 0, 0),
                Duration.ofMinutes(60));
        Epic epic = new Epic("E", "D");
        Subtask subtask = new Subtask("S", "D", epic,
                LocalDateTime.of(2022, 7, 14, 11, 0, 0),
                Duration.ofMinutes(60));
        Subtask subtask2 = new Subtask("S2", "D2", epic,
                LocalDateTime.of(2022, 7, 15, 11, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createTask(task);
        tasksManager.createEpic(epic);
        tasksManager.createSubtask(epic, subtask);
        tasksManager.createSubtask(epic, subtask2);

        tasksManager.getTaskByID(task.getId());
        tasksManager.getEpicByID(epic.getId());
        tasksManager.getSubtaskByID(subtask.getId());
        tasksManager.getSubtaskByID(subtask2.getId());
        assertEquals(Arrays.asList(task, epic, subtask, subtask2), tasksManager.getHistory());

        assertNotEquals(Arrays.asList(task, epic, subtask, subtask2, tasksManager.getTaskByID(-15)),
                tasksManager.getHistory());
    }

    @Test
    public void isIntersect() {
        Task task = new Task("T", "D", Status.NEW,
                LocalDateTime.of(2022, 12, 15, 10, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createTask(task);
        Task task2 = new Task("T2", "D2", Status.NEW,
                LocalDateTime.of(2022, 12, 15, 10, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createTask(task2);
        assertNull(tasksManager.getTaskByID(task2.getId()));

        Epic epic = new Epic("E", "D");
        tasksManager.createEpic(epic);
        Subtask subtask = new Subtask("S", "D", epic,
                LocalDateTime.of(2022, 12, 15, 10, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createSubtask(epic, subtask);
        assertNull(tasksManager.getSubtaskByID(subtask.getId()));

        Task task3 = new Task("T2", "D2", Status.NEW,
                LocalDateTime.of(2022, 12, 15, 11, 0, 1),
                Duration.ofMinutes(60));
        tasksManager.createTask(task3);
        assertEquals(task3, tasksManager.getTaskByID(task3.getId()));
    }

    @Test
    public void getPrioritizedTasks() {
        Task task = new Task("T", "D", Status.NEW,
                LocalDateTime.of(2022, 12, 15, 10, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createTask(task);
        Task task2 = new Task("T2", "D2", Status.NEW,
                LocalDateTime.of(2022, 12, 14, 10, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createTask(task2);
        Epic epic = new Epic("E", "D");
        tasksManager.createEpic(epic);
        Subtask subtask = new Subtask("S", "D", epic,
                LocalDateTime.of(2022, 12, 10, 10, 0, 0),
                Duration.ofMinutes(60));
        tasksManager.createSubtask(epic, subtask);
        assertEquals(List.of(subtask, task2, task), tasksManager.getPrioritizedTasks());
    }
}
