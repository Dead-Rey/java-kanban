import main.java.controllers.InMemoryTaskManager;
import main.java.controllers.model.Epic;
import main.java.controllers.model.Progress;
import main.java.controllers.model.SubTask;
import main.java.controllers.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach

    public void setUp() {
        taskManager = new InMemoryTaskManager(); // Инициализация конкретной реализации
    }

    @Test
    public void testAddTask() {
        Task task = new Task("Task 1", "Description 1", Progress.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        super.testAddTask(task); // Вызов метода из абстрактного класса
    }

    @Test
    public void testAddEpic() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        super.testAddEpic(epic); // Вызов метода из абстрактного класса
    }

    @Test
    public void testAddSubtaskWithEpic() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask 1", "Subtask Description",Progress.NEW, epic.getId(),
                Duration.ofHours(1), LocalDateTime.now());
        super.testAddSubtaskWithEpic(subTask, epic); // Вызов метода из абстрактного класса
    }

    @Test
    public void testDeleteTaskById() {
        Task task = new Task("Task 1", "Description 1", Progress.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        taskManager.addTask(task);
        taskManager.deleteTaskById(task.getId());

        assertNull(taskManager.getTaskById(task.getId()));
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void testDeleteEpicWithSubtasks() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask 1", "Subtask Description", Progress.NEW, epic.getId(),
                Duration.ofHours(1), LocalDateTime.now());
        taskManager.addSubtask(subTask);

        taskManager.deleteEpic();

        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    public void testGetHistory() {
        Task task = new Task("Task 1", "Description 1", Progress.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        super.testGetHistory(task); // Вызов метода из абстрактного класса
    }

    @Test
    public void testTaskOverlap() {
        Task task1 = new Task("Task 1", "Description 1",Progress.NEW,
                Duration.ofHours(2),LocalDateTime.of(2023, 10, 1, 10, 0));
        Task task2 = new Task("Task 2", "Description 2", Progress.NEW, Duration.ofHours(2),
                LocalDateTime.of(2023, 10, 1, 11, 0));

        taskManager.addTask(task1);
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2));
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Task 1", "Description 1", Progress.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        taskManager.addTask(task);

        Task updatedTask = new Task("Updated Task", "Updated Description",
                Progress.DONE, Duration.ofHours(1), LocalDateTime.now().plusHours(2));
        taskManager.updateTask(task, updatedTask);

        assertEquals(updatedTask, taskManager.getTaskById(task.getId()));
    }

    @Test
    public void testUpdateSubTask() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask 1", "Subtask Description", Progress.NEW, epic.getId(),
                Duration.ofHours(1), LocalDateTime.now());
        taskManager.addSubtask(subTask);

        SubTask updatedSubTask = new SubTask("Updated Subtask", "Updated Description",
                Progress.IN_PROGRESS, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        taskManager.updateSubTask(subTask, updatedSubTask);

        assertEquals(updatedSubTask, taskManager.getSubtaskByEpic(epic).get(0));
    }
    @Test
    public void testDeleteAllTasks() {
        // Добавляем несколько задач
        Task task1 = new Task("Task 1", "Description 1", Progress.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", Progress.NEW,
                Duration.ofHours(2), LocalDateTime.now().plusHours(2));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Теперь вызываем метод удаления всех задач из абстрактного класса
        super.testDeleteTask(); // Вызов метода из абстрактного класса
    }
}



