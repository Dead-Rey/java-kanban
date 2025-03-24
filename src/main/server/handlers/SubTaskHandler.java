package main.server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import main.java.controllers.TaskManager;
import main.java.controllers.model.SubTask;
import main.java.controllers.model.Task;
import main.server.Endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static main.server.Endpoints.*;

public class SubTaskHandler extends BaseHttpHandler {

    TaskManager taskManager;

    public SubTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Endpoints endpoint = getEndpoints(path, exchange.getRequestMethod());
        switch (endpoint) {
            case GET -> getSubTasks(exchange);
            case GET_BY_ID -> getSubTask(exchange, getId(path));
            case POST -> postSubTask(exchange);
            case DELETE_BY_ID -> deleteSubTask(exchange, getId(path));
            default -> sendBadRequest(exchange);
        }
    }

    private Endpoints getEndpoints(String requestPath, String requestMethod) {
        String[] paths = requestPath.split("/");
        if (paths.length <= 3 && paths[1].equals("subtasks")) {
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

    private void getSubTasks(HttpExchange exchange) throws IOException {
        String jsonString = gson.toJson(taskManager.getSubtasks());
        if (jsonString.isEmpty()) {
            sendNotFound(exchange);
        }
        sendText(exchange, jsonString, 200);
    }

    private void getSubTask(HttpExchange exchange, int id) throws IOException {
        Optional<Task> jsonString = taskManager.getTaskById(id);
        if (jsonString.isPresent()) {
            sendText(exchange, gson.toJson(jsonString.get()), 200);
        } else {
            sendNotFound(exchange);
        }
    }

    private void postSubTask(HttpExchange exchange) throws IOException {
        InputStream stream = exchange.getRequestBody();
        String strTask = new String(stream.readAllBytes());

        if (strTask.isEmpty()) {
            sendBadRequest(exchange); // Используем 400 Bad Request вместо 404
            return;
        }

        SubTask subTask;
        try {
            subTask = gson.fromJson(strTask, SubTask.class);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange); // Ошибка парсинга JSON
            return;
        }

        try {
            // Проверяем на пересечение задач перед добавлением

            if (subTask.getId() == 0) {
                taskManager.addTask(subTask);
                sendText(exchange, "Задача успешно добавлена", 201);
                exchange.sendResponseHeaders(201, 0);
            } else {
                Optional<Task> taskOptional = taskManager.getTaskById(subTask.getId());
                if (taskOptional.isPresent()) {
                    SubTask oldSubTask = (SubTask) taskOptional.get();
                    taskManager.updateTask(oldSubTask, subTask);
                    sendText(exchange, "Задача успешно обновлена", 201);
                    exchange.sendResponseHeaders(201, 0);
                } else {
                    sendNotFound(exchange); // Задача не найдена для обновления
                }
            }
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange); // Обработка конфликта задач
        }
    }

    private void deleteSubTask(HttpExchange exchange, int id) throws IOException {
        if (taskManager.getTaskById(id).isPresent()) {
            taskManager.deleteTaskById(id);
            sendText(exchange, "Задача успешно удалена", 200);
        } else {
            sendNotFound(exchange);
        }
    }

}