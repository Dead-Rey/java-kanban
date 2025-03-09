import main.fileManagers.FileBackedTaskManager;
import main.java.controllers.InMemoryTaskManager;
import main.java.controllers.model.Epic;
import main.java.controllers.model.Progress;
import main.java.controllers.model.SubTask;
import main.java.controllers.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File testFile;

    @BeforeEach

    public void setUp() {
        testFile = new File("test_file.csv");
        taskManager = new FileBackedTaskManager(testFile);
    }

    @AfterEach
    public void tearDown() {
        if (testFile.exists()) {
            testFile.delete(); // Удаляем файл после теста
        }
    }

    @Test
    public void testAddTask() {
        Task task = new Task("Test Task", "Description", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        testAddTask(task);
    }

    @Test
    public void testAddEpic() {
        Epic epic = new Epic("Test Epic", "Epic Description");
        testAddEpic(epic);
    }

    @Test
    public void testAddSubtaskWithEpic() {
        Epic epic = new Epic("Test Epic", "Epic Description");
        SubTask subTask = new SubTask("Test Subtask", "Subtask Description",
                Progress.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        testAddSubtaskWithEpic(subTask, epic);
    }

    @Test
    public void testSaveAndLoadTasks() {
        InMemoryTaskManager.setIdCounter(1);
        Task task1 = new Task("Task 1", "Description 1", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Subtask 1", "Subtask Description",
                Progress.NEW, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusHours(2));
        taskManager.addSubtask(subTask);
        epic.addSubtask(subTask);


        taskManager.save(); // Сохраняем в файл

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        System.out.println(loadedManager);

        assertEquals(2, loadedManager.getTasks().size());
        assertEquals(task1, loadedManager.getTaskById(task1.getId()));
        assertEquals(task2, loadedManager.getTaskById(task2.getId()));
        assertEquals(1, loadedManager.getEpics().size());
        assertEquals(epic, loadedManager.getEpics().get(0));
        assertEquals(1, loadedManager.getSubtasks().size());
        assertEquals(subTask, loadedManager.getSubtasks().get(0));
    }

    @Test
    public void testDeleteTask() {
        Task task = new Task("Task to delete", "Description",
                Progress.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);
        taskManager.deleteTask();
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void testGetHistory() {
        Task task = new Task("Task for history", "Description",
                Progress.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId()); // доступ к задаче
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }
}
