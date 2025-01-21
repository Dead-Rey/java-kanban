package tests;

import controllers.InMemoryTaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;


import java.util.List;

import static model.Progress.*;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    //Тест создания задачи
    private void addAnyTask(Task anyTask) {
        taskManager.addTask(anyTask);

        final Task savedTask = taskManager.getTaskById(anyTask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(anyTask, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(anyTask, tasks.get(0), "Задачи не совпадают.");
    }

        @Test
            void addNewTaskTest() {
                Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
                addAnyTask(task);
        }

        @Test
            void addNewEpicTest() {
                Task epic = new Epic("Test addNewTask", "Test addNewTask description");
                addAnyTask(epic);
        }

        @Test
            void addNewSubtaskTest(){
                Task subtask = new SubTask("Test addNewTask", "Test addNewTask description", NEW,1);
                addAnyTask(subtask);
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
            void findTaskByIdTest() {
                Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
                Task epic = new Epic("Test addNewTask", "Test addNewTask description");
                Task subtask = new SubTask("Test addNewTask", "Test addNewTask description", NEW,epic.getId());
                taskManager.addTask(task);
                taskManager.addTask(epic);
                taskManager.addTask(subtask);
                assertEquals(epic, taskManager.getTaskById(2));
        }


}