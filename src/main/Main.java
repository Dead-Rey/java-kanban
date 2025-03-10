package main;

import main.java.controllers.Managers;
import main.java.controllers.TaskManager;
import main.java.controllers.model.Epic;
import main.java.controllers.model.Progress;
import main.java.controllers.model.SubTask;
import main.java.controllers.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

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
                "Понять что нужно сделать",Progress.NEW, epic1.getId(),Duration.ofMinutes(30),
                LocalDateTime.now().plus(Duration.ofHours(3)));
        SubTask subTask2 = new SubTask("Написать код",
                "Применить знания полученные при обучении", Progress.NEW, epic1.getId(),
                Duration.ofMinutes(30), LocalDateTime.now().plus(Duration.ofHours(4)));
        SubTask subTask3 = new SubTask("Купить продукты",
                "Сходить в магазин", Progress.NEW, epic1.getId(),Duration.ofMinutes(30),
                LocalDateTime.now().plus(Duration.ofHours(5)));
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);
        taskManager.addSubtask(subTask3); //Добавление подзадач в менеджер

        epic1.addSubtask(subTask1);
        epic1.addSubtask(subTask2);
        epic2.addSubtask(subTask3); //Добавление подзадач в эпик

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(epic1.getId());
        taskManager.getTaskById(epic2.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(subTask1.getId());
        taskManager.getTaskById(subTask2.getId());
        taskManager.getTaskById(subTask3.getId());
        taskManager.getTaskById(subTask1.getId());

        printAllTasks(taskManager);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(subTask1.getId());

        printAllTasks(taskManager);

        taskManager.deleteTaskById(task1.getId());

        printAllTasks(taskManager);

        taskManager.deleteTaskById(epic1.getId());

        printAllTasks(taskManager);

    }

    public static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtaskByEpic(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

}
