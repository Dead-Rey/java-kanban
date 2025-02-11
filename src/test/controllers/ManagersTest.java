package controllers;

import model.Progress;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    //Тест создания менеджера через утилитарный класс
    @Test
    void getDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);
    }

    //Тест равенства возвращаемых значений утилитарного класса и метода объекта
    @Test
    void getDefaultHistory() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("TestTask_1","TestTaskDescription", Progress.NEW);
        taskManager.addTask(task1);
        assertNotNull(Managers.getDefaultHistory());
    }
}