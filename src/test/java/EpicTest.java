import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    Epic epic;

    @BeforeEach
    public void beforeEach() {
        epic = new Epic("Epic 1", "description Epic 1");
    }

    public void addSubtasks(Status status1, Status status2) {
        Subtask subtask = new Subtask(2, "Subtask", "description Subtask 1", status1, epic.getId(),
                LocalDateTime.of(2022, 7, 13, 0, 0, 0),
                Duration.ofMinutes(60));
        Subtask subtask2 = new Subtask(3, "Subtask 2", "description Subtask 2", status2,
                epic.getId(), LocalDateTime.of(2022, 7, 13, 0, 0, 0),
                Duration.ofMinutes(60));

        epic.addSubtask(subtask);
        epic.addSubtask(subtask2);
    }

    @Test
    public void statusWithEmptySubtaskList() {
        assertTrue(epic.getSubtaskList().isEmpty());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void statusWithAllNewSubtasks() {
        addSubtasks(Status.NEW, Status.NEW);

        assertEquals(2, epic.getSubtaskList().size());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void statusWithAllDoneSubtasks() {
        addSubtasks(Status.DONE, Status.DONE);

        assertEquals(2, epic.getSubtaskList().size());
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void statusWithDoneAndNewSubtasks() {
        addSubtasks(Status.DONE, Status.NEW);

        assertEquals(2, epic.getSubtaskList().size());
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void statusWithInProgressSubtasks() {
        addSubtasks(Status.IN_PROGRESS, Status.IN_PROGRESS);

        assertEquals(2, epic.getSubtaskList().size());
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}