import main.java.controllers.InMemoryHistoryManager;
import main.java.controllers.model.Progress;
import main.java.controllers.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void testAddTaskToHistory() {
        Task task = new Task("Test Task", "Description", Progress.NEW, Duration.ofMinutes(30),
                LocalDateTime.now());
        task.setId(1);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void testRemoveTaskFromHistory() {
        Task task1 = new Task("Test Task 1", "Description", Progress.NEW, Duration.ofMinutes(30),
                LocalDateTime.now());
        task1.setId(1);
        Task task2 = new Task("Test Task 2", "Description", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    public void testGetEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    public void testLinkLast() {
        Task task1 = new Task("Test Task 1", "Description", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        task1.setId(1);
        Task task2 = new Task("Test Task 2", "Description", Progress.NEW, Duration.ofMinutes(30),
                LocalDateTime.now());
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    public void testRemoveNode() {
        Task task1 = new Task("Test Task 1", "Description", Progress.NEW, Duration.ofMinutes(30),
                LocalDateTime.now());
        task1.setId(1);
        Task task2 = new Task("Test Task 2", "Description", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        task2.setId(2);
        Task task3 = new Task("Test Task 3", "Description", Progress.NEW, Duration.ofMinutes(30),
                LocalDateTime.now());
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    public void testAddSameTask() {
        Task task = new Task("Test Task", "Description", Progress.NEW, Duration.ofMinutes(30),
                LocalDateTime.now());
        task.setId(1);
        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }
}
