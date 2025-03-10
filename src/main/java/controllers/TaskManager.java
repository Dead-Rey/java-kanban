package main.java.controllers;

import main.java.controllers.model.Epic;
import main.java.controllers.model.SubTask;
import main.java.controllers.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface TaskManager {

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(SubTask subtask);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubtasks();

    void deleteTask();

    void deleteSubtask();

    void deleteEpic();

    Optional<Task> getTaskById(int id);

    void deleteTaskById(int id);

    ArrayList<SubTask> getSubtaskByEpic(Epic epic);

    void updateTask(Task task, Task taskNew);

    void updateSubTask(SubTask subTask, SubTask subTaskNew);

    List<Task> getHistory();

}

