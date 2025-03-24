package main.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import main.java.controllers.TaskManager;
import main.java.controllers.model.Epic;
import main.java.controllers.model.SubTask;
import main.java.controllers.model.Task;
import main.server.Endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;

import static main.server.Endpoints.*;

public class EpicHandler extends BaseHttpHandler {

    TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Endpoints endpoint = getEndpoints(path, exchange.getRequestMethod());
        switch (endpoint) {
            case GET -> getEpics(exchange);
            case GET_BY_ID -> getTask(exchange, getId(path));
            case GET_SUBTASKS_BY_EPIC_ID -> getSubtasksById(exchange, getId(path));
            case POST -> postEpic(exchange);
            case DELETE_BY_ID -> deleteTask(exchange, getId(path));
            default -> sendBadRequest(exchange);
        }
    }

    private Endpoints getEndpoints(String requestPath, String requestMethod) {
        String[] paths = requestPath.split("/");
        if (paths.length <= 4 && paths[1].equals("epics")) {
            switch (requestMethod) {
                case "GET":
                    if (paths.length == 2) {
                        return GET;
                    } else if (paths.length == 3) {
                        return GET_BY_ID;
                    } else {
                        return GET_SUBTASKS_BY_EPIC_ID;
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

    private void getEpics(HttpExchange exchange) throws IOException {
        String jsonString = gson.toJson(taskManager.getEpics());
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

    private void getSubtasksById(HttpExchange exchange, int id) throws IOException {
        String strEpic = new String(exchange.getRequestBody().readAllBytes());
        Epic epic = gson.fromJson(strEpic, Epic.class);
        ArrayList<SubTask> subTasks = taskManager.getSubtaskByEpic(epic);
        if (subTasks.isEmpty()) {
            sendNotFound(exchange);
        } else {
            sendText(exchange, gson.toJson(subTasks), 200);
        }
    }

    private void postEpic(HttpExchange exchange) throws IOException {
        InputStream stream = exchange.getRequestBody();
        String strTask = new String(stream.readAllBytes());
        if (strTask.isEmpty()) {
            sendNotFound(exchange);
        }
        Epic epic = gson.fromJson(strTask, Epic.class);
        taskManager.addEpic(epic);
        sendText(exchange, "Задача успешно добавлена", 201);
        exchange.sendResponseHeaders(201, 0);

    }

    private void deleteTask(HttpExchange exchange, int id) throws IOException {
        if (taskManager.getTaskById(id).isPresent()) {
            taskManager.deleteTaskById(id);
            sendText(exchange, "Задача успешно удалена", 201);
        } else {
            sendNotFound(exchange);
        }
    }

}
