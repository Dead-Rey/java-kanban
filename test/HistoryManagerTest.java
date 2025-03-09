import main.java.controllers.HistoryManager;
import main.java.controllers.InMemoryHistoryManager;
import main.java.controllers.model.Task;
import main.java.controllers.model.Progress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager(); // Предполагается, что у вас есть реализация HistoryManager
    }

    @Test
    void testAddTaskToHistory() {
        Task task1 = new Task("Test Task 1", "Description 1", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void testEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    void testDuplicateTask() {
        Task task1 = new Task("Test Task 1", "Description 1", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        historyManager.add(task1);
        historyManager.add(task1); // Добавляем дубликат

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История не должна содержать дубликаты");
        assertEquals(task1, history.get(0));
    }

    @Test
    void testRemoveTaskFromHistoryBeginning() {
        Task task1 = new Task("Test Task 1", "Description 1", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Test Task 2", "Description 2", Progress.NEW,
                Duration.ofMinutes(45), LocalDateTime.now());
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1); // Удаляем первую задачу

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void testRemoveTaskFromHistoryMiddle() {
        Task task1 = new Task("Test Task 1", "Description 1", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        task1.setId(1);
        Task task2 = new Task("Test Task 2", "Description 2", Progress.NEW,
                Duration.ofMinutes(45), LocalDateTime.now().plusHours(2));
        task2.setId(2);
        Task task3 = new Task("Test Task 3", "Description 3", Progress.NEW,
                Duration.ofMinutes(60), LocalDateTime.now().plusHours(3));
        task3.setId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2); // Удаляем вторую задачу

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    void testRemoveTaskFromHistoryEnd() {
        Task task1 = new Task("Test Task 1", "Description 1", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        task1.setId(1);
        Task task2 = new Task("Test Task 2", "Description 2", Progress.NEW,
                Duration.ofMinutes(45), LocalDateTime.now());
        task2.setId(2);
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task2.getId()); // Удаляем последнюю задачу

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void testRemoveNonExistentTask() {
        Task task1 = new Task("Test Task 1", "Description 1", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        historyManager.add(task1);

        historyManager.remove(2); // Пытаемся удалить несуществующую задачу

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна остаться неизменной");
        assertEquals(task1, history.get(0));
    }
}
