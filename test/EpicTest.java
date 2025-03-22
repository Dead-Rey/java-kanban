import main.java.controllers.model.Epic;
import main.java.controllers.model.Progress;
import main.java.controllers.model.SubTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private Epic epic;

    @BeforeEach
    public void setUp() {
        epic = new Epic("Epic Name", "Epic Description");
    }

    @Test
    public void testAllSubtasksNew() {
        // Создаем подзадачи со статусом NEW
        SubTask subtask1 = new SubTask("Subtask 1", "Description 1", Progress.NEW, epic.getId(), Duration.ofMinutes(30), null);
        SubTask subtask2 = new SubTask("Subtask 2", "Description 2", Progress.NEW, epic.getId(), Duration.ofMinutes(45), null);

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        // Проверяем статус эпика
        assertEquals(Progress.NEW, epic.getProgress());
    }

    @Test
    public void testAllSubtasksDone() {
        // Создаем подзадачи со статусом DONE
        SubTask subtask1 = new SubTask("Subtask 1", "Description 1", Progress.DONE, epic.getId(), Duration.ofMinutes(30), null);
        SubTask subtask2 = new SubTask("Subtask 2", "Description 2", Progress.DONE, epic.getId(), Duration.ofMinutes(45), null);

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        // Проверяем статус эпика
        assertEquals(Progress.DONE, epic.getProgress());
    }

    @Test
    public void testSubtasksNewAndDone() {
        // Создаем подзадачи со статусами NEW и DONE
        SubTask subtask1 = new SubTask("Subtask 1", "Description 1", Progress.NEW, epic.getId(), Duration.ofMinutes(30), null);
        SubTask subtask2 = new SubTask("Subtask 2", "Description 2", Progress.DONE, epic.getId(), Duration.ofMinutes(45), null);

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        // Проверяем статус эпика
        assertEquals(Progress.IN_PROGRESS, epic.getProgress());
    }

    @Test
    public void testSubtasksInProgress() {
        // Создаем подзадачи со статусом IN_PROGRESS
        SubTask subtask1 = new SubTask("Subtask 1", "Description 1", Progress.IN_PROGRESS, epic.getId(), Duration.ofMinutes(30), null);
        SubTask subtask2 = new SubTask("Subtask 2", "Description 2", Progress.IN_PROGRESS, epic.getId(), Duration.ofMinutes(45), null);

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        // Проверяем статус эпика
        assertEquals(Progress.IN_PROGRESS, epic.getProgress());
    }

    @Test
    public void testCleanSubtasks() {
        // Добавляем подзадачи и очищаем их
        SubTask subtask1 = new SubTask("Subtask 1", "Description 1", Progress.NEW, epic.getId(), Duration.ofMinutes(30), null);
        epic.addSubtask(subtask1);

        // Проверяем статус перед очисткой
        assertEquals(Progress.NEW, epic.getProgress());

        // Очищаем подзадачи
        epic.cleanSubtasks();

        // Проверяем статус после очистки
        assertEquals(Progress.NEW, epic.getProgress());
        assertTrue(epic.getSubtasks().isEmpty());
    }
}