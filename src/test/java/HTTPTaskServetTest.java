import http.HttpTaskServer;
import http.KVServer;
import managers.TasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.IOException;

import com.google.gson.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskServetTest {
    String url = "http://localhost:8080";
    HttpTaskServer httpTaskServer;
    KVServer kvServer;
    private static final Gson gson = new Gson();

    @BeforeEach
    public void startHTTPTaskServer() {
        assertDoesNotThrow(() -> kvServer = new KVServer());
        kvServer.start();
        assertDoesNotThrow(() -> httpTaskServer = new HttpTaskServer());
        httpTaskServer.start();
        TasksManager manager = httpTaskServer.tasksManager;

        Task task1 = new Task("Task1", "description Task1",
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        Task task2 = new Task("Task2", "description Task2",
                LocalDateTime.of(2022, 7, 13, 11, 0, 0),
                Duration.ofMinutes(60));
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Epic1", "description Epic");
        Subtask subtask1 = new Subtask("Subtask1", "description Subtask1", epic1,
                LocalDateTime.of(2022, 7, 15, 11, 0, 0),
                Duration.ofMinutes(60));
        manager.createEpic(epic1);
        manager.createSubtask(epic1, subtask1);

        manager.getSubtaskByID(subtask1.getId());
        manager.getTaskByID(task1.getId());
        manager.getEpicByID(epic1.getId());
    }

    @AfterEach
    public void stopHTTPTaskServer() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    private HttpResponse<String> makeRequestGET(String path) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + path))
                .GET()
                .header("Accept", "application/json")
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> makeRequestPost(String path, String value) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + path))
                .POST(HttpRequest.BodyPublishers.ofString(value))
                .header("Accept", "application/json")
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> makeRequestDelete(String path) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + path))
                .DELETE()
                .header("Accept", "application/json")
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void getPrioritizedTasks() {
        assertDoesNotThrow(() -> {
            HttpResponse<String> response = makeRequestGET("/tasks");
            assertEquals(200, response.statusCode());
            String tasks = gson.toJson(httpTaskServer.tasksManager.getPrioritizedTasks());
            assertEquals(tasks, response.body());
        });
    }

    @Test
    public void getTasks() {
        assertDoesNotThrow(() -> {
            HttpResponse<String> response = makeRequestGET("/tasks/task/");
            assertEquals(200, response.statusCode());
            String tasks = gson.toJson(httpTaskServer.tasksManager.getTasks());
            assertEquals(tasks, response.body());
        });
    }

    @Test
    public void getTasksById() {
        assertDoesNotThrow(() -> {
            HttpResponse<String> response = makeRequestGET("/tasks/task/?id=1");
            assertEquals(200, response.statusCode());
            String task = gson.toJson(httpTaskServer.tasksManager.getTaskByID(1));
            assertEquals(task, response.body());
        });
    }

    @Test
    public void getSubtasks() {
        assertDoesNotThrow(() -> {
            HttpResponse<String> response = makeRequestGET("/tasks/subtask/");
            assertEquals(200, response.statusCode());
            String subtasks = gson.toJson(httpTaskServer.tasksManager.getSubtasks());
            assertEquals(subtasks, response.body());
        });
    }

    @Test
    public void getSubtaskById() {
        assertDoesNotThrow(() -> {
            HttpResponse<String> response = makeRequestGET("/tasks/subtask/?id=4");
            assertEquals(200, response.statusCode());
            String subtask = gson.toJson(httpTaskServer.tasksManager.getSubtaskByID(4));
            assertEquals(subtask, response.body());
        });
    }

    @Test
    public void getEpics() {
        assertDoesNotThrow(() -> {
            HttpResponse<String> response = makeRequestGET("/tasks/epic/");
            assertEquals(200, response.statusCode());
            String epics = gson.toJson(httpTaskServer.tasksManager.getEpics());
            assertEquals(epics, response.body());
        });
    }

    @Test
    public void getEpicById() {
        assertDoesNotThrow(() -> {
            HttpResponse<String> response = makeRequestGET("/tasks/epic/?id=3");
            assertEquals(200, response.statusCode());
            String epic = gson.toJson(httpTaskServer.tasksManager.getEpicByID(3));
            assertEquals(epic, response.body());
        });
    }

    @Test
    public void getHistory() {
        assertDoesNotThrow(() -> {
            HttpResponse<String> response = makeRequestGET("/tasks/history/");
            assertEquals(200, response.statusCode());
            String history = gson.toJson(httpTaskServer.tasksManager.getHistory());
            assertEquals(history, response.body());
        });
    }

    @Test
    public void getEpicSubtasksList() {
        assertDoesNotThrow(() -> {
            HttpResponse<String> response = makeRequestGET("/tasks/subtask/epic/?id=2");
            assertEquals(200, response.statusCode());
            String history = gson.toJson(httpTaskServer.tasksManager.getEpicSubtasksList(httpTaskServer.
                    tasksManager.getEpicByID(2)));
            assertEquals(history, response.body());
        });
    }

    @Test
    public void clearTasks() {
        assertDoesNotThrow(() -> {
            HttpResponse<String> response = makeRequestDelete("/tasks/task/");
            assertEquals(200, response.statusCode());
            assertEquals("Tasks clear", response.body());
        });
    }

    @Test
    public void removeTask() {
        assertDoesNotThrow(() -> {
            HttpResponse<String> response = makeRequestDelete("/tasks/task/?id=1");
            assertEquals(200, response.statusCode());
            assertEquals("Task remove", response.body());
        });
    }

    @Test
    public void clearEpics() {
        assertDoesNotThrow(() -> {
            HttpResponse<String> response = makeRequestDelete("/tasks/epic/");
            assertEquals(200, response.statusCode());
            assertEquals("Epics clear", response.body());
        });
    }

    @Test
    public void removeEpic() {
        assertDoesNotThrow(() -> {
            HttpResponse<String> response = makeRequestDelete("/tasks/epic/?id=2");
            assertEquals(200, response.statusCode());
            assertEquals("Epic remove", response.body());
        });
    }

    @Test
    public void clearSubtasks() {
        assertDoesNotThrow(() -> {
            HttpResponse<String> response = makeRequestDelete("/tasks/subtask/");
            assertEquals(200, response.statusCode());
            assertEquals("Subtasks clear", response.body());
        });
    }

    @Test
    public void removeSubtask() {
        assertDoesNotThrow(() -> {
            HttpResponse<String> response = makeRequestDelete("/tasks/subtask/?id=3");
            assertEquals(200, response.statusCode());
            assertEquals("Subtask remove", response.body());
        });
    }

    @Test
    public void createTask() {
        assertDoesNotThrow(() -> {
            Task task = new Task("NEW TASK", "DESCRIPTION NEW TASK",
                    LocalDateTime.of(2022, 7, 15, 19, 0, 0),
                    Duration.ofMinutes(60));
            String taskString = gson.toJson(task);
            HttpResponse<String> response = makeRequestPost("/tasks/task/",
                    taskString);
            assertEquals(200, response.statusCode());
            assertEquals("Task create", response.body());
            System.out.println(httpTaskServer.tasksManager.getTasks());
        });
    }

    @Test
    public void updateTask() {
        assertDoesNotThrow(() -> {
            Task task = new Task(1, "OLD TASK", "DESCRIPTION OLD TASK", Status.IN_PROGRESS,
                    LocalDateTime.of(2022, 7, 15, 19, 0, 0),
                    Duration.ofMinutes(60));
            String taskString = gson.toJson(task);
            HttpResponse<String> response = makeRequestPost("/tasks/task/",
                    taskString);
            assertEquals(200, response.statusCode());
            assertEquals("Task Update", response.body());
            System.out.println(httpTaskServer.tasksManager.getTasks());
        });
    }

    @Test
    public void createEpic() {
        assertDoesNotThrow(() -> {
            Epic epic = new Epic("NEW EPIC", "DESCRIPTION NEW EPIC");

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Epic.class, new EpicAdapter())
                    .create();
            String taskString = gson.toJson(epic);
            HttpResponse<String> response = makeRequestPost("/tasks/epic/",
                    taskString);
            assertEquals(200, response.statusCode());
            assertEquals("Epic create", response.body());
        });
    }

    @Test
    public void updateEpic() {
        assertDoesNotThrow(() -> {
            Epic epic = new Epic(2, "NEW EPIC", "DESCRIPTION NEW EPIC", Status.IN_PROGRESS);

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Epic.class, new EpicAdapter())
                    .create();
            String taskString = gson.toJson(epic);
            HttpResponse<String> response = makeRequestPost("/tasks/epic/",
                    taskString);
            assertEquals(200, response.statusCode());
            assertEquals("Epic update", response.body());
            System.out.println(httpTaskServer.tasksManager.getEpics());
        });
    }

    @Test
    public void createSubtask() {
        assertDoesNotThrow(() -> {
            Subtask subtask = new Subtask("NEW SUBTASK", "DESCRIPTION NEW SUBTASK",
                    httpTaskServer.tasksManager.getEpicByID(2),
                    LocalDateTime.of(2022, 7, 15, 19, 0, 0),
                    Duration.ofMinutes(60));
            String taskString = gson.toJson(subtask);
            HttpResponse<String> response = makeRequestPost("/tasks/subtask/",
                    taskString);
            assertEquals(200, response.statusCode());
            assertEquals("Subtask create", response.body());
        });
    }

    @Test
    public void updateSubtask() {
        assertDoesNotThrow(() -> {
            Subtask subtask = new Subtask(3, "UPDATE SUBTASK", "DESCRIPTION UPDATE SUBTASK",
                    Status.IN_PROGRESS,
                    2,
                    LocalDateTime.of(2022, 7, 15, 19, 0, 0),
                    Duration.ofMinutes(60));
            String taskString = gson.toJson(subtask);
            HttpResponse<String> response = makeRequestPost("/tasks/subtask/",
                    taskString);
            assertEquals(200, response.statusCode());
            assertEquals("Subtask update", response.body());
            System.out.println(httpTaskServer.tasksManager.getSubtasks());
        });
    }
}
