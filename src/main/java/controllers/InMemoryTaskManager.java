package main.java.controllers;

import main.exception.TaskOverlapException;
import main.java.controllers.model.Epic;
import main.java.controllers.model.SubTask;
import main.java.controllers.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager  {
    private static int idCounter = 1;
    private final HashMap<Integer, Task> tasks;

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
        task.setId(idCounter);
        tasks.put(idCounter++, task);
        Set<Task> prioritizedTasks = getPrioritizedTasks();

        try {
            doTaskOverlap((TreeSet<Task>) prioritizedTasks); // Проверка перекрытия
        } catch (TaskOverlapException e) {
            throw new IllegalArgumentException("Задача пересекается с другой задачей: " + e.getMessage());
        }


    }


   @Override
   public void addEpic(Epic epic) { // Добавление эпика + присваивание уникального ID
       epic.setId(idCounter);
       epics.put(idCounter++, epic);
   }

    @Override
    public void addSubtask(SubTask subtask) {
        Set<Task> prioritizedTasks = getPrioritizedTasks();

        try {
            doTaskOverlap((TreeSet<Task>) prioritizedTasks); // Проверка перекрытия
        } catch (TaskOverlapException e) {
            throw new IllegalArgumentException("Подзадача пересекается с существующей подзадачей: " + e.getMessage());
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

    public Set<Task> getPrioritizedTasks() {
        Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        tasks.values().stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(prioritizedTasks::add);

        subtasks.values().stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(prioritizedTasks::add);
        return prioritizedTasks;

    }

    public void doTaskOverlap(TreeSet<Task> prioritizedTasks) throws TaskOverlapException {
        if (prioritizedTasks.isEmpty()) {
            return; // Если нет задач, просто выходим
        }

        // Получаем итератор для доступа к задачам
        Iterator<Task> iterator = prioritizedTasks.iterator();
        Task previousTask = iterator.next(); // Получаем первую задачу

        while (iterator.hasNext()) {
            Task currentTask = iterator.next();

            // Проверяем, пересекаются ли временные отрезки
            if (previousTask.getEndTime().isAfter(currentTask.getStartTime())) {
                throw new TaskOverlapException("Перекрытие задач: " + previousTask.getName() + " и " + currentTask.getName());
            }

            // Обновляем previousTask для следующей итерации
            previousTask = currentTask;
        }
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

        Set<Task> prioritizedTasks = getPrioritizedTasks();
        prioritizedTasks.remove(task);

        try {
            doTaskOverlap((TreeSet<Task>) prioritizedTasks); // Проверка перекрытия
        } catch (TaskOverlapException e) {
            throw new IllegalArgumentException("Задача пересекается с существующей задачей: " + e.getMessage());
        }

        tasks.put(task.getId(), taskNew);
    }

    @Override
    public void updateSubTask(SubTask subTask, SubTask subTaskNew) {
        if (!subtasks.containsKey(subTask.getId())) {
            throw new IllegalArgumentException("Подзадача с указанным ID не найдена");
        }

        Set<Task> prioritizedTasks = getPrioritizedTasks();
        prioritizedTasks.remove(subTask);

        try {
            doTaskOverlap((TreeSet<Task>) prioritizedTasks); // Проверка перекрытия
        } catch (TaskOverlapException e) {
            throw new IllegalArgumentException("Подзадача пересекается с существующей подзадачей: " + e.getMessage());
        }

        subtasks.put(subTask.getId(), subTaskNew);
        Epic epic = epics.get(subTask.getEpicId());
        epic.removeSubtask(subTask);
        epic.addSubtask(subTaskNew);
        epics.put(epic.getId(), epic);
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
