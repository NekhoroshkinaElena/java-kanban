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
        if (isGet(httpExchange)) {
            SendOkResponse(httpExchange, gson.toJson(tasksManager.getPrioritizedTasks()));
        }
        sendBadRequest(httpExchange, "");
    }

    public void task(HttpExchange httpExchange) throws IOException {
        if (isGet(httpExchange)) {
            String query = httpExchange.getRequestURI().getQuery();
            if (query == null) {
                SendOkResponse(httpExchange, gson.toJson(tasksManager.getTasks()));
            } else {
                int id = queryToMap(query);
                SendOkResponse(httpExchange, gson.toJson(tasksManager.getTaskByID(id)));
            }
        }
        if (isPost(httpExchange)) {
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Task task;

            try {
                task = gson.fromJson(body, Task.class);
            } catch (JsonSyntaxException e) {
                sendBadRequest(httpExchange, e.getMessage());
                return;
            }

            if (tasksManager.getTaskByID(task.getId()) == null) {
                tasksManager.createTask(task);
                if (tasksManager.getTaskByID(task.getId()) != null) {
                    SendOkResponse(httpExchange, "Task create");
                } else {
                    sendBadRequest(httpExchange, "Задача не была создана, так как она " +
                            "пересекается по времени с другой задачей");
                }
            } else {
                tasksManager.updateTask(task);
                SendOkResponse(httpExchange, "Task Update");
            }
        }
        if (isDelete(httpExchange)) {
            String query = httpExchange.getRequestURI().getQuery();
            if (query == null) {
                tasksManager.clearTasks();
                if (tasksManager.getTasks().isEmpty()) {
                    SendOkResponse(httpExchange, "Tasks clear");
                }
            } else {
                int id = queryToMap(query);
                tasksManager.removeTask(id);
                if (tasksManager.getTaskByID(id) == null) {
                    SendOkResponse(httpExchange, "Task remove");
                }
            }
        }
    }

    public void subtask(HttpExchange httpExchange) throws IOException {
        if (isGet(httpExchange)) {
            String query = httpExchange.getRequestURI().getQuery();
            if (query == null) {
                SendOkResponse(httpExchange, gson.toJson(tasksManager.getSubtasks()));
            }
            if (query != null) {
                int id = queryToMap(query);
                SendOkResponse(httpExchange, gson.toJson(tasksManager.getSubtaskByID(id)));
            }
        }
        if (isPost(httpExchange)) {
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask;

            try {
                subtask = gson.fromJson(body, Subtask.class);
            } catch (JsonSyntaxException e) {
                sendBadRequest(httpExchange, e.getMessage());
                return;
            }

            if (tasksManager.getSubtaskByID(subtask.getId()) == null) {
                tasksManager.createSubtask(tasksManager.getEpicByID(subtask.getEpicId()), subtask);
                if (tasksManager.getSubtaskByID(subtask.getId()) != null) {
                    SendOkResponse(httpExchange, "Subtask create");
                }
            } else if (tasksManager.getSubtaskByID(subtask.getId()) != null) {
                tasksManager.updateSubtask(subtask);
                SendOkResponse(httpExchange, "Subtask update");
            }
        }
        if (isDelete(httpExchange)) {
            String query = httpExchange.getRequestURI().getQuery();
            if (query == null) {
                tasksManager.clearSubtasks();
                if (tasksManager.getSubtasks().isEmpty()) {
                    SendOkResponse(httpExchange, "Subtasks clear");
                }
            } else {
                int id = queryToMap(query);
                tasksManager.removeSubtask(id);
                if (tasksManager.getSubtaskByID(id) == null) {
                    SendOkResponse(httpExchange, "Subtask remove");
                }
            }
        }
    }

    public void epic(HttpExchange httpExchange) throws IOException {
        if (isGet(httpExchange)) {
            String query = httpExchange.getRequestURI().getQuery();
            if (query == null) {
                SendOkResponse(httpExchange, gson.toJson(tasksManager.getEpics()));
            }
            if (query != null) {
                int id = queryToMap(query);
                SendOkResponse(httpExchange, gson.toJson(tasksManager.getEpicByID(id)));
            }
        }
        if (isPost(httpExchange)) {
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Epic.class, new EpicAdapter())
                    .create();

            Epic epic;
            try {
                epic = gson.fromJson(body, Epic.class);
            } catch (JsonSyntaxException e) {
                sendBadRequest(httpExchange, e.getMessage());
                return;
            }

            if (tasksManager.getEpicByID(epic.getId()) == null) {
                tasksManager.createEpic(epic);
                if (tasksManager.getEpicByID(epic.getId()) != null) {
                    SendOkResponse(httpExchange, "Epic create");
                }
            } else if (tasksManager.getEpicByID(epic.getId()) != null) {
                tasksManager.updateEpic(epic);
                SendOkResponse(httpExchange, "Epic update");
            }
        }
        if (isDelete(httpExchange)) {
            String query = httpExchange.getRequestURI().getQuery();
            if (query == null) {
                tasksManager.clearEpics();
                if (tasksManager.getEpics().isEmpty()) {
                    SendOkResponse(httpExchange, "Epics clear");
                }
            } else {
                int id = queryToMap(query);
                tasksManager.removeEpic(id);
                if (tasksManager.getEpicByID(id) == null) {
                    SendOkResponse(httpExchange, "Epic remove");
                }
            }
        }
    }

    public void subtaskEpic(HttpExchange httpExchange) throws IOException {
        if (isGet(httpExchange)) {
            String query = httpExchange.getRequestURI().getQuery();
            if (!query.isEmpty()) {
                int id = queryToMap(query);
                SendOkResponse(httpExchange, gson.toJson(tasksManager.getEpicSubtasksList(tasksManager.getEpicByID(id))));
            }
        }
    }

    public void history(HttpExchange httpExchange) throws IOException {
        if (isGet(httpExchange)) {
            SendOkResponse(httpExchange, gson.toJson(tasksManager.getHistory()));
        }
    }

    private boolean isGet(HttpExchange httpExchange) {
        return "GET".equals(httpExchange.getRequestMethod());
    }

    private boolean isPost(HttpExchange httpExchange) {
        return "POST".equals(httpExchange.getRequestMethod());
    }

    private boolean isDelete(HttpExchange httpExchange) {
        return "DELETE".equals(httpExchange.getRequestMethod());
    }

    private void SendOkResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void sendBadRequest(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(400, 0);
        if (!response.isEmpty()) {
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





