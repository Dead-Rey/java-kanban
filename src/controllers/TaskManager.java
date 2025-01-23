package controllers;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

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

    Task getTaskById(int id);

    void deleteTaskById(int id);

    ArrayList<SubTask> getSubtaskByEpic(Epic epic);

    void updateTask(Task task, Task taskNew);

    void updateSubTask(SubTask subTask, SubTask subTaskNew);

    List<Task> getHistory();

}

