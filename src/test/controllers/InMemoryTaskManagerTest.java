package test.controllers;

import controllers.InMemoryTaskManager;
import controllers.TaskManager;
import model.Epic;
import model.Progress;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testAddTask() {
        Task task = new Task("Test Task", "Description", Progress.NEW);
        taskManager.addTask(task);
        assertEquals(1, taskManager.getTasks().size());
        assertEquals(task, taskManager.getTasks().get(0));
    }

    @Test
    public void testAddEpic() {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(epic, taskManager.getEpics().get(0));
    }

    @Test
    public void testAddSubtask() {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Test Subtask", "Description", Progress.NEW, epic.getId());
        taskManager.addSubtask(subTask);
        assertEquals(1, taskManager.getSubtasks().size());
        assertEquals(subTask, taskManager.getSubtasks().get(0));
    }

    @Test
    public void testDeleteTask() {
        Task task = new Task("Test Task", "Description", Progress.NEW);
        taskManager.addTask(task);
        taskManager.deleteTask();
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void testDeleteEpic() {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);
        taskManager.deleteEpic();
        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    public void testGetTaskById() {
        Task task = new Task("Test Task", "Description", Progress.NEW);
        taskManager.addTask(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertEquals(task, retrievedTask);
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Test Task", "Description", Progress.NEW);
        taskManager.addTask(task);
        Task updatedTask = new Task("Updated Task", "New Description", Progress.IN_PROGRESS);
        updatedTask.setId(task.getId());
        taskManager.updateTask(task, updatedTask);
        assertEquals(updatedTask, taskManager.getTaskById(task.getId()));
    }

    @Test
    public void testGetHistory() {
        Task task = new Task("Test Task", "Description", Progress.NEW);
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        assertEquals(1, taskManager.getHistory().size());
        assertEquals(task, taskManager.getHistory().get(0));
    }

    @Test
    public void testGetSubtaskByEpic() {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Test Subtask", "Description", Progress.NEW, epic.getId());
        taskManager.addSubtask(subTask);
        epic.addSubtask(subTask);
        ArrayList<SubTask> subtasks = taskManager.getSubtaskByEpic(epic);
        assertEquals(1, subtasks.size());
        assertEquals(subTask, subtasks.get(0));
    }
}
