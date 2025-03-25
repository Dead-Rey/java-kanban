package main.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import main.java.controllers.TaskManager;
import main.server.Endpoints;

import java.io.IOException;

import static main.server.Endpoints.GET;

public class PrioritizedHandler extends BaseHttpHandler {

    private TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Endpoints endpoint = getEndpoints(path, exchange.getRequestMethod());
        if (endpoint != null) {
            getPrioritized(exchange);
        } else {
            sendMethodNotAllowed(exchange);
        }
    }

    private Endpoints getEndpoints(String requestPath, String requestMethod) {
        String[] paths = requestPath.split("/");
        if (paths.length <= 2 && paths[1].equals("prioritized")) {
            if (requestMethod.equals("GET")) {
                return GET;
            }
            return null;
        }
        return null;
    }

    private void getPrioritized(HttpExchange exchange) throws IOException {
        String jsonString = gson.toJson(taskManager.getPrioritizedTasks());
        if (jsonString.isEmpty()) {
            sendNotFound(exchange);
        }
        sendText(exchange, jsonString, 200);
    }
}
