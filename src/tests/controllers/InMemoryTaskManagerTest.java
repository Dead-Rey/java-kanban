package tests.controllers;

import controllers.Managers;
import controllers.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;

import static model.Progress.*;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {


    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    //Тест создания задачи
    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);

        taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
        void addNewSubTask() {
        SubTask subTask = new SubTask("Test addNewTask", "Test addNewTask description", NEW, 1);

        taskManager.addSubtask(subTask);

        final Task savedTask = taskManager.getTaskById(subTask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subTask, savedTask, "Задачи не совпадают.");

        final ArrayList<SubTask> tasks = taskManager.getSubtasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(subTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
        void addNewEpic(){
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");

        taskManager.addEpic(epic);

        final Task savedTask = taskManager.getTaskById(epic.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");

        final ArrayList<Epic> tasks = taskManager.getEpics();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(epic, tasks.get(0), "Задачи не совпадают.");
    }

        //Тест удаления всех задач
        @Test
            void removeTaskTest() {
                Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
                taskManager.addTask(task);
                taskManager.addTask(task);
                taskManager.deleteTask();
                assertTrue(taskManager.getTasks().isEmpty());
        }

        //Тест удаления задач по ID
        @Test
            void removeTaskByIdTest() {
                Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
                Task task2 = new Task("Test addNewTask 2", "Test addNewTask description 2", NEW);
                taskManager.addTask(task);
                taskManager.addTask(task2);
                taskManager.deleteTaskById(1);
                assertFalse(taskManager.getTasks().contains(task));
        }

        //Поиск задачи по ID
        @Test
            void  findTaskByIdTest() {
                Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
                Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
                SubTask subtask = new SubTask("Test addNewTask", "Test addNewTask description", NEW,epic.getId());
                taskManager.addTask(task);
                taskManager.addEpic(epic);
                taskManager.addSubtask(subtask);
                assertEquals(epic, taskManager.getTaskById(epic.getId()),"Не найдено");
        }
}