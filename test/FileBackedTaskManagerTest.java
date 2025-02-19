import main.fileManagers.FileBackedTaskManager;
import main.java.controllers.model.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;
    private File tempFile;

    // Создание временного файла
    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("task_manager_test", ".txt");
        manager = new FileBackedTaskManager(tempFile);
    }

    // Тест загрузки и сохранения нескольких задач
    @Test
    void testSaveAndLoad() {
        Epic epic = new Epic("Test Epic", "Epic description");
        manager.addEpic(epic);
        SubTask subtask = new SubTask("Test Subtask", "Subtask description", Progress.NEW, epic.getId());
        manager.addSubtask(subtask);
        Task task = new Task("Test Task", "Description of test task", Progress.DONE);
        manager.addTask(task);
        manager.save(); // Сохраняем текущее состояние в файл

        // Создаем новый менеджер и загружаем из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что загруженная задача соответствует сохраненной
        assertEquals(1, loadedManager.getTasks().size());
        assertEquals(task, loadedManager.getTasks().getFirst());
    }

    // Тест на сохранение и загрузку пустого файла
    @Test
    void testSaveAndLoadEmptyFile(){
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getTasks().isEmpty());
    }
}
