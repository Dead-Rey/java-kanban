package main.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import main.java.controllers.TaskManager;
import main.server.Endpoints;

import java.io.IOException;
import java.util.Objects;

import static main.server.Endpoints.GET;

public class HistoryHandler extends BaseHttpHandler {

    TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Endpoints endpoint = getEndpoints(path, exchange.getRequestMethod());
        if (Objects.requireNonNull(endpoint) == GET) {
            getHistory(exchange);
        } else {
            sendBadRequest(exchange);
        }
    }

    private Endpoints getEndpoints(String requestPath, String requestMethod) {
        String[] paths = requestPath.split("/");
        if (paths.length <= 2 && paths[1].equals("history")) {
            if (requestMethod.equals("GET")) {
                return GET;
            }
            return null;
        }
        return null;
    }

    private void getHistory(HttpExchange exchange) throws IOException {
        String jsonString = gson.toJson(taskManager.getHistory());
        if (jsonString.isEmpty()) {
            sendNotFound(exchange);
        }
        sendText(exchange, jsonString, 200);
    }
}