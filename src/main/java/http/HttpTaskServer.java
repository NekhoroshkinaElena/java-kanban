package http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TasksManager;
import tasks.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Gson gson = new Gson();
    public TasksManager tasksManager = Managers.getDefault();
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", this::tasksPrioritized);
        server.createContext("/tasks/task/", this::task);
        server.createContext("/tasks/subtask/", this::subtask);
        server.createContext("/tasks/epic/", this::epic);
        server.createContext("/tasks/history/", this::history);
        server.createContext("/tasks/subtask/epic/", this::subtaskEpic);
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        server.stop(0);
    }

    public void tasksPrioritized(HttpExchange httpExchange) throws IOException {
        if ("GET".equals(httpExchange.getRequestMethod())) {
            String response = gson.toJson(tasksManager.getPrioritizedTasks());
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
        }
        httpExchange.sendResponseHeaders(400, 0);
    }

    public void task(HttpExchange httpExchange) throws IOException {
        if ("GET".equals(httpExchange.getRequestMethod())) {
            String query = httpExchange.getRequestURI().getQuery();
            if (query == null) {
                String response = gson.toJson(tasksManager.getTasks());
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                int id = queryToMap(query);
                String response = gson.toJson(tasksManager.getTaskByID(id));
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        if ("POST".equals(httpExchange.getRequestMethod())) {
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Task task;

            try {
                task = gson.fromJson(body, Task.class);
            } catch (JsonSyntaxException e) {
                httpExchange.sendResponseHeaders(400, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(e.getMessage().getBytes(StandardCharsets.UTF_8));
                }
                return;
            }

            if (tasksManager.getTaskByID(task.getId()) == null) {
                tasksManager.createTask(task);
                if (tasksManager.getTaskByID(task.getId()) != null) {
                    String response = "Task create";
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } else {
                    String response = "Задача не была создана, так как она " +
                            "пересекается по времени с другой задачей";
                    httpExchange.sendResponseHeaders(400, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                }
            } else {
                tasksManager.updateTask(task);
                String response = "Task Update";
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        if ("DELETE".equals(httpExchange.getRequestMethod())) {
            String query = httpExchange.getRequestURI().getQuery();
            if (query == null) {
                tasksManager.clearTasks();
                if (tasksManager.getTasks().isEmpty()) {
                    String response = "Tasks clear";
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                }
            } else {
                int id = queryToMap(query);
                tasksManager.removeTask(id);
                if (tasksManager.getTaskByID(id) == null) {
                    String response = "Task remove";
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        }
    }

    public void subtask(HttpExchange httpExchange) throws IOException {
        if ("GET".equals(httpExchange.getRequestMethod())) {
            String query = httpExchange.getRequestURI().getQuery();
            if (query == null) {
                String response = gson.toJson(tasksManager.getSubtasks());
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }
            if (query != null) {
                int id = queryToMap(query);
                String response = gson.toJson(tasksManager.getSubtaskByID(id));
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        if ("POST".equals(httpExchange.getRequestMethod())) {
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask;

            try {
                subtask = gson.fromJson(body, Subtask.class);
            } catch (JsonSyntaxException e) {
                httpExchange.sendResponseHeaders(400, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(e.getMessage().getBytes(StandardCharsets.UTF_8));
                }
                return;
            }

            if (tasksManager.getSubtaskByID(subtask.getId()) == null) {
                tasksManager.createSubtask(tasksManager.getEpicByID(subtask.getEpicId()), subtask);
                if (tasksManager.getSubtaskByID(subtask.getId()) != null) {
                    String response = "Subtask create";
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                }
            } else if (tasksManager.getSubtaskByID(subtask.getId()) != null) {
                tasksManager.updateSubtask(subtask);
                String response = "Subtask update";
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        if ("DELETE".equals(httpExchange.getRequestMethod())) {
            String query = httpExchange.getRequestURI().getQuery();
            if (query == null) {
                tasksManager.clearSubtasks();
                if (tasksManager.getSubtasks().isEmpty()) {
                    String response = "Subtasks clear";
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                }
            } else {
                int id = queryToMap(query);
                tasksManager.removeSubtask(id);
                if (tasksManager.getSubtaskByID(id) == null) {
                    String response = "Subtask remove";
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        }
    }

    public void epic(HttpExchange httpExchange) throws IOException {
        if ("GET".equals(httpExchange.getRequestMethod())) {
            String query = httpExchange.getRequestURI().getQuery();
            if (query == null) {
                String response = gson.toJson(tasksManager.getEpics());
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }
            if (query != null) {
                int id = queryToMap(query);
                String response = gson.toJson(tasksManager.getEpicByID(id));
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        if ("POST".equals(httpExchange.getRequestMethod())) {
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Epic.class, new EpicAdapter())
                    .create();

            Epic epic;
            try {
                epic = gson.fromJson(body, Epic.class);
            } catch (JsonSyntaxException e) {
                httpExchange.sendResponseHeaders(400, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(e.getMessage().getBytes(StandardCharsets.UTF_8));
                }
                return;
            }

            if (tasksManager.getEpicByID(epic.getId()) == null) {
                tasksManager.createEpic(epic);
                if (tasksManager.getEpicByID(epic.getId()) != null) {
                    String response = "Epic create";
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                }
            } else if (tasksManager.getEpicByID(epic.getId()) != null) {
                tasksManager.updateEpic(epic);
                String response = "Epic update";
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        if ("DELETE".equals(httpExchange.getRequestMethod())) {
            String query = httpExchange.getRequestURI().getQuery();
            if (query == null) {
                tasksManager.clearEpics();
                if (tasksManager.getEpics().isEmpty()) {
                    String response = "Epics clear";
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                }
            } else {
                int id = queryToMap(query);
                tasksManager.removeEpic(id);
                if (tasksManager.getEpicByID(id) == null) {
                    String response = "Epic remove";
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        }
    }

    public void subtaskEpic(HttpExchange httpExchange) throws IOException {
        if ("GET".equals(httpExchange.getRequestMethod())) {
            String query = httpExchange.getRequestURI().getQuery();
            if (!query.isEmpty()) {
                int id = queryToMap(query);
                String response = gson.toJson(tasksManager.getEpicSubtasksList(tasksManager.getEpicByID(id)));
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
    }

    public void history(HttpExchange httpExchange) throws IOException {
        if ("GET".equals(httpExchange.getRequestMethod())) {
            String response = gson.toJson(tasksManager.getHistory());
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    public int queryToMap(String query) {
        int id = 0;
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                id = Integer.parseInt(entry[1]);
            } else {
                id = Integer.parseInt(entry[0]);
            }
        }
        return id;
    }
}





