package main.java.controllers;

import main.exception.TaskOverlapException;
import main.java.controllers.model.Epic;
import main.java.controllers.model.SubTask;
import main.java.controllers.model.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager  {
    private static int idCounter = 1;
    private final HashMap<Integer, Task> tasks;
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public static void setIdCounter(int idCounter) {
        InMemoryTaskManager.idCounter = idCounter;
    }

    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subtasks;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() { // Конструктор для всех типов
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public void addTask(Task task) {

        try {
            doTaskOverlap(task); // Проверка перекрытия
        } catch (TaskOverlapException e) {
            throw new IllegalArgumentException("Задача: " + e.getMessage() + " пересекается с существующей задачей.");
        }
        // Если пересечений не найдено, добавляем задачу в мапу и отсортированный список
        task.setId(idCounter);
        tasks.put(idCounter++, task);
        prioritizedTasks.add(task);
    }


   @Override
   public void addEpic(Epic epic) { // Добавление эпика + присваивание уникального ID
       epic.setId(idCounter);
       epics.put(idCounter++, epic);
   }

    @Override
    public void addSubtask(SubTask subtask) {

        try {
            doTaskOverlap(subtask); // Проверка перекрытия
        } catch (TaskOverlapException e) {
            throw new IllegalArgumentException("Задача: " + e.getMessage() + " пересекается с существующей задачей.");
        }

        subtask.setId(idCounter);
        subtasks.put(idCounter++, subtask);

        prioritizedTasks.add(subtask);

        Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtask(subtask);
    }


    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteTask() { // Удаление всех задач
        tasks.values().forEach(task -> {
            historyManager.remove(task.getId()); // Удаляем из истории
            prioritizedTasks.remove(task); // Удаляем из приоритетных задач
        });
        tasks.clear(); // Очистка всех задач
    }

    @Override
    public void deleteSubtask() {  // Удаление всех подзадач
        // Удаление подзадач из истории
        subtasks.values().stream()
                .map(SubTask::getId) // Получаем ID подзадач
                .filter(historyManager.getHistory()::contains) // Фильтруем только те, которые есть в истории
                .forEach(historyManager::remove); // Удаляем из истории
        subtasks.values().forEach(prioritizedTasks::remove); // удаляем подзадачи из отсортированного списка
        // Удаление всех подзадач из эпиков и обновление их прогресса
        epics.values().forEach(epic -> {
            epic.cleanSubtasks(); // Удаление всех подзадач из эпиков
            epic.updateProgress(); // Обновление прогресса эпиков
        });

        // Очистка всех подзадач
        subtasks.clear();
    }


    @Override
    public void deleteEpic() { // Удаление эпиков и подзадач как следствие
        Epic epicToDelete = epics.values().stream().findFirst().orElse(null);
        if (epicToDelete != null) {
            epicToDelete.getSubtasks().forEach(subtask -> {
                historyManager.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
                subtasks.remove(subtask.getId());
            });
            historyManager.remove(epicToDelete.getId());
            epics.remove(epicToDelete.getId());
        }
    }


    @Override
    public Optional<Task> getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return Optional.of(tasks.get(id));
        } else if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return Optional.of(subtasks.get(id));
        } else if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return Optional.of(epics.get(id));
        }
        return Optional.empty();
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
        } else if (subtasks.containsKey(id)) {
            SubTask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);
            epic.removeSubtask(subtask);
            subtasks.remove(id);
            epic.updateProgress();
        } else if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (SubTask subtask : epic.getSubtasks()) {
                historyManager.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
                subtasks.remove(subtask.getId());
            }
            historyManager.remove(id);
            epic.cleanSubtasks();
            epics.remove(id);
        }
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public void doTaskOverlap(Task task) throws TaskOverlapException {
        if (task.getStartTime() == null || task.getDuration() == null) {
            return; // Если пусто или нет времени, выходим
        }

        if (prioritizedTasks.isEmpty()) {
            prioritizedTasks.add(task);
            return; // Если список задач пуст, добавляем новую задачу
        }

        final LocalDateTime startTime = task.getStartTime();
        final LocalDateTime endTime = task.getEndTime();

        for (Task t : prioritizedTasks) {
            final LocalDateTime existStart = t.getStartTime();
            final LocalDateTime existEnd = t.getEndTime();

            // Проверяем, пересекаются ли задачи
            if (endTime.isAfter(existStart) && startTime.isBefore(existEnd)) {
                throw new TaskOverlapException("Задача пересекается с id=" + t.getId() + " с " + existStart + " по " + existEnd);
            }
        }

        // Если не было пересечений, добавляем задачу
        prioritizedTasks.add(task);
    }

    @Override
        public ArrayList<SubTask> getSubtaskByEpic(Epic epic) { // Получение подзадач определенного эпика
        return epic.getSubtasks();
        }

    @Override
    public void updateTask(Task task, Task taskNew) {
        if (!tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Задача с указанным ID не найдена");
        }

        try {
            doTaskOverlap(taskNew); // Проверка перекрытия
        } catch (TaskOverlapException e) {
            throw new IllegalArgumentException("Задача: " + e.getMessage() + " пересекается с существующей задачей.");
        }

        tasks.put(task.getId(), taskNew);
        prioritizedTasks.remove(task);
        prioritizedTasks.add(taskNew);
    }

    @Override
    public void updateSubTask(SubTask subTask, SubTask subTaskNew) {
        if (!subtasks.containsKey(subTask.getId())) {
            throw new IllegalArgumentException("Подзадача с указанным ID не найдена");
        }

        try {
            prioritizedTasks.remove(subTask);
            doTaskOverlap(subTaskNew); // Проверка перекрытия
        } catch (TaskOverlapException e) {
            prioritizedTasks.add(subTask);
            throw new IllegalArgumentException("Задача: " + e.getMessage() + " пересекается с существующей задачей.");
        }

        subtasks.put(subTask.getId(), subTaskNew);
        Epic epic = epics.get(subTask.getEpicId());
        epic.removeSubtask(subTask);
        epic.addSubtask(subTaskNew);
        epics.put(epic.getId(), epic);
        prioritizedTasks.remove(subTask);
        prioritizedTasks.add(subTaskNew);
    }

        @Override
        public List<Task> getHistory() {
        return historyManager.getHistory();
        }



    @Override
    public String toString() {
        return "controllers.TaskManager{" + "\n" +
                "Tasks=" + tasks + "\n" +
                "Epics=" + epics + "\n" +
                "Subtasks=" + subtasks +
                '}';
    }
}
