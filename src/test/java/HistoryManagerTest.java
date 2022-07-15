import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import managers.HistoryManager;
import managers.Managers;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class HistoryManagerTest {

    HistoryManager historyManager;

    @Test
    public void emptyHistoryTask() {
        historyManager = Managers.getDefaultHistory();
        assertEquals(0, historyManager.getHistory().size());

        Task task = new Task(1, "T", "d", Status.NEW,
                LocalDateTime.of(2022, 5, 1, 12, 0, 0),
                Duration.ofMinutes(60));

        historyManager.remove(task.getId());
        assertEquals(0, historyManager.getHistory().size());

        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());

        historyManager.remove(task.getId());
        assertEquals(new ArrayList<>(), historyManager.getHistory());
    }

    @Test
    public void duplicateHistoryTask() {
        historyManager = Managers.getDefaultHistory();

        Task task = new Task(1, "T", "d", Status.NEW, null,
                Duration.ofMinutes(60));
        Epic epic = new Epic(2, "E", "D", Status.NEW);

        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(epic);

        assertEquals(2, historyManager.getHistory().size());

        historyManager.remove(task.getId());
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void removeFromHistory() {
        historyManager = Managers.getDefaultHistory();

        Task task = new Task(1, "T", "d", Status.NEW, null,
                Duration.ofMinutes(60));
        Epic epic = new Epic(2, "E", "D", Status.NEW);
        Subtask subtask = new Subtask(3, "S", "D", Status.NEW, epic.getId(), null,
                Duration.ofMinutes(60));
        Subtask subtask2 = new Subtask(4, "S2", "D2", Status.NEW, epic.getId(), null,
                Duration.ofMinutes(60));

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(subtask2);
        assertEquals(4, historyManager.getHistory().size());

        historyManager.remove(task.getId());
        assertEquals(List.of(epic, subtask, subtask2), historyManager.getHistory());

        historyManager.remove(subtask.getId());
        assertEquals(List.of(epic, subtask2), historyManager.getHistory());

        historyManager.remove(subtask2.getId());
        assertEquals(List.of(epic), historyManager.getHistory());
    }
}