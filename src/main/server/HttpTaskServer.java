package main.server;

import com.sun.net.httpserver.HttpServer;
import main.java.controllers.Managers;
import main.java.controllers.TaskManager;
import main.java.controllers.model.Epic;
import main.java.controllers.model.Progress;
import main.java.controllers.model.SubTask;
import main.java.controllers.model.Task;
import main.server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private static final int PORT = 8080;
    HttpServer server;
    TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubTaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
    }

    public static void main(String[] args) throws Exception {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Покормить кота", "Насыпать корм в миску", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plus(Duration.ofHours(1)));
        Task task2 = new Task("Сделать зарядку", "Зарядка в 9:00", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plus(Duration.ofHours(2)));
        taskManager.addTask(task1);
        taskManager.addTask(task2); //Добавление обычных задач в менеджер

        Epic epic1 = new Epic("Сделать ФЗ", "Решить задачу");
        Epic epic2 = new Epic("Отметить новый год", "Найти настроение");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2); //Добавление эпиков в менеджер

        SubTask subTask1 = new SubTask("Разобраться с задачей",
                "Понять что нужно сделать", Progress.NEW, epic1.getId(), Duration.ofMinutes(30),
                LocalDateTime.now().plus(Duration.ofHours(3)));
        SubTask subTask2 = new SubTask("Написать код",
                "Применить знания полученные при обучении", Progress.NEW, epic1.getId(),
                Duration.ofMinutes(30), LocalDateTime.now().plus(Duration.ofHours(4)));
        SubTask subTask3 = new SubTask("Купить продукты",
                "Сходить в магазин", Progress.NEW, epic2.getId(), Duration.ofMinutes(30),
                LocalDateTime.now().plus(Duration.ofHours(5)));
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);
        taskManager.addSubtask(subTask3);

        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();
    }

    public void start() {
        server.start();
        System.out.println("Server started on port " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped");
    }
}
