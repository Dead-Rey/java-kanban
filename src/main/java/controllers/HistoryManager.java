package main.java.controllers;

import main.java.controllers.model.Task;

import java.util.List;

public interface HistoryManager {

    //Метод добавления задачи в список
    void add(Task task);

    // Метод получения истории
    List<Task> getHistory();

    // Метод очистки истории по ID
    void remove(int id);

    void linkLast(Task task);

    List<Task> getTasks();

    void removeNode(Node node);
}
