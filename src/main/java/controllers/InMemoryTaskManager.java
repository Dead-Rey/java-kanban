package main.java.controllers;

import main.exception.TaskOverlapException;
import main.java.controllers.model.Epic;
import main.java.controllers.model.SubTask;
import main.java.controllers.model.Task;

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
            throw new IllegalArgumentException("Задача: " + e.getMessage() + " пересекается с существующей задачей." );
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
            throw new IllegalArgumentException("Задача: " + e.getMessage() + " пересекается с существующей задачей." );
        }

        subtask.setId(idCounter);
        subtasks.put(idCounter++, subtask);
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
    public void deleteTask()  { // Удаление всех задач
        tasks.clear();
    }

    @Override
    public void deleteSubtask() {  // Удаление всех подзадач
        // Удаление подзадач из истории
        subtasks.values().stream()
                .map(SubTask::getId) // Получаем ID подзадач
                .filter(historyManager.getHistory()::contains) // Фильтруем только те, которые есть в истории
                .forEach(historyManager::remove); // Удаляем из истории

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
        // Удаление эпиков из истории
        epics.values().stream()
                .map(Epic::getId) // Получаем ID эпиков
                .filter(historyManager.getHistory()::contains) // Фильтруем только те, которые есть в истории
                .forEach(historyManager::remove); // Удаляем из истории

        // Удаление подзадач из истории
        subtasks.values().stream()
                .map(SubTask::getId) // Получаем ID подзадач
                .filter(historyManager.getHistory()::contains) // Фильтруем только те, которые есть в истории
                .forEach(historyManager::remove); // Удаляем из истории

        // Очистка всех эпиков и подзадач
        epics.clear();
        subtasks.clear();
    }


    @Override
    public Task getTaskById(int id) { // Получение любой задачи по ID
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        } else if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        }
        return null;
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            tasks.remove(id);
        } else if (subtasks.containsKey(id)) {
            SubTask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());

            historyManager.remove(id);
            epic.removeSubtask(subtask);
            subtasks.remove(id);
            epic.updateProgress();
        } else if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (SubTask subtask : epic.getSubtasks()) {
                historyManager.remove(subtask.getId());
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
        if (prioritizedTasks.isEmpty() || task.getStartTime() == null || task.getDuration() == null) {
            return; // Если пусто, или нет времени, то сразу выходим
        }
            if (prioritizedTasks.getFirst().getStartTime().isAfter(task.getEndTime()) ||
                    prioritizedTasks.getLast().getEndTime().isBefore(task.getStartTime())) {
                return;
            }
        throw new TaskOverlapException(task.getName());

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
            throw new IllegalArgumentException("Задача: " + e.getMessage() + " пересекается с существующей задачей." );
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
            doTaskOverlap(subTaskNew); // Проверка перекрытия
        } catch (TaskOverlapException e) {
            throw new IllegalArgumentException("Задача: " + e.getMessage() + " пересекается с существующей задачей." );
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
