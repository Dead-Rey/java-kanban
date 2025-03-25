package main.server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import main.java.controllers.TaskManager;
import main.java.controllers.model.Task;
import main.server.Endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static main.server.Endpoints.*;

public class TaskHandler extends BaseHttpHandler {

    private TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Endpoints endpoint = getEndpoints(path, exchange.getRequestMethod());
        switch (endpoint) {
            case GET -> getTasks(exchange);
            case GET_BY_ID -> getTask(exchange, getId(path));
            case POST -> postTask(exchange);
            case DELETE_BY_ID -> deleteTask(exchange, getId(path));
            default -> sendMethodNotAllowed(exchange);
        }
    }

    private Endpoints getEndpoints(String requestPath, String requestMethod) {
        String[] paths = requestPath.split("/");
        if (paths.length <= 3 && paths[1].equals("tasks")) {
            switch (requestMethod) {
                case "GET":
                    if (paths.length == 2) {
                        return GET;
                    } else {
                        return GET_BY_ID;
                    }
                case "POST":
                    return POST;
                case "DELETE":
                    return DELETE_BY_ID;
                default:
                    return null;
            }
        }
        return null;
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        String jsonString = gson.toJson(taskManager.getTasks());
        if (jsonString.isEmpty()) {
            sendNotFound(exchange);
        }
        sendText(exchange, jsonString, 200);
    }

    private void getTask(HttpExchange exchange, int id) throws IOException {
        Optional<Task> jsonString = taskManager.getTaskById(id);
        if (jsonString.isPresent()) {
            sendText(exchange, gson.toJson(jsonString.get()), 200);
        } else {
            sendNotFound(exchange);
        }
    }

    private void postTask(HttpExchange exchange) throws IOException {
        InputStream stream = exchange.getRequestBody();
        String strTask = new String(stream.readAllBytes());

        if (strTask.isEmpty()) {
            sendNotFound(exchange);
            return;
        }

        Task task;
        try {
            task = gson.fromJson(strTask, Task.class);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange);
            return;
        }

        try {
            if (task.getId() == 0) {
                taskManager.addTask(task);
                sendText(exchange, "Задача успешно добавлена", 201);

            } else {
                Optional<Task> taskOptional = taskManager.getTaskById(task.getId());
                if (taskOptional.isPresent()) {
                    Task oldTask = taskOptional.get();
                    taskManager.updateTask(oldTask, task);
                    sendText(exchange, "Задача успешно обновлена", 201);
                    exchange.sendResponseHeaders(201, 0);
                } else {
                    sendNotFound(exchange);
                }
            }
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        }
    }


    private void deleteTask(HttpExchange exchange, int id) throws IOException {
        if (taskManager.getTaskById(id).isPresent()) {
            taskManager.deleteTaskById(id);
            sendText(exchange, "Задача успешно удалена", 200);
        } else {
            sendNotFound(exchange);
        }
    }

}
