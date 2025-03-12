import main.java.controllers.TaskManager;
import main.java.controllers.model.Epic;
import main.java.controllers.model.Task;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    // Тест для добавления задачи
    public void testAddTask(Task task) {
        taskManager.addTask(task);
        assertEquals(1, taskManager.getTasks().size());
        Optional<Task> retrievedTaskOptional = taskManager.getTaskById(task.getId());
        assertTrue(retrievedTaskOptional.isPresent());
        Task retrievedTask = retrievedTaskOptional.get();
        assertEquals(task, retrievedTask);
    }

    // Тест для добавления эпика
    public void testAddEpic(Epic epic) {
        taskManager.addEpic(epic);
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(epic, taskManager.getEpics().get(0));
    }

    // Тест для удаления всех задач
    public void testDeleteTask() {
        taskManager.deleteTask();
        assertEquals(0, taskManager.getTasks().size());
    }

    // Тест для получения истории
    public void testGetHistory(Task task) {
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId()); // доступ к задаче
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

}
