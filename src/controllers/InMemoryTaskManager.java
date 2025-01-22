package controllers;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager  {
    private static int idCounter = 1;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subtasks;
    HistoryManager historyManager = new InMemoryHistoryManager();

    public InMemoryTaskManager() { // Конструктор для всех типов
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public void addTask(Task task) { // Добавление задачи + присваивание уникального ID
        task.setId(idCounter);
        tasks.put(idCounter++, task);

    }

   @Override
   public void addEpic(Epic epic) { // Добавление эпика + присваивание уникального ID
       epic.setId(idCounter);
       epics.put(idCounter++, epic);
   }

    @Override
    public void addSubtask(SubTask subtask) { // Добавление подзадачи + присваивание уникального ID
        subtask.setId(idCounter);
        subtasks.put(idCounter++, subtask);
    }

    @Override
    public ArrayList<Task> getTasks() {
        if (tasks.isEmpty()) {
            return new ArrayList<>();
        }
        Task task = tasks.values().iterator().next();
        historyManager.add(task);
        return new ArrayList<>(tasks.values());
    }
    @Override
    public ArrayList<Epic> getEpics() {
        if (epics.isEmpty()) {
            return new ArrayList<>();
        }
        Task epic = epics.values().iterator().next();
        historyManager.add(epic);
        return new ArrayList<>(epics.values());
    }
    @Override
    public ArrayList<SubTask> getSubtasks() {
        if (subtasks.isEmpty()) {
            return new ArrayList<>();
        }
        Task subtask = subtasks.values().iterator().next();
        historyManager.add(subtask);
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteTask()  { // Удаление всех задач
        tasks.clear();
    }
    @Override
    public void deleteSubtask() {  // Удаление всех подзадач
        for (Epic epic : epics.values()) {
            epic.cleanSubtasks(); // Удаление всех подзадач из эпиков
            epic.updateProgress(); // Обновление прогресса эпиков
        }
        subtasks.clear();
    }
    @Override
    public void deleteEpic() { // Удаление эпиков и подзадач как следствие
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) { // Получение любой задачи по ID
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    @Override
    public void deleteTaskById(int id) { // Удаление любой задачи по ID
        if (tasks.containsKey(id)){
            tasks.remove(id);
        } else
            if (subtasks.containsKey(id)) {
                SubTask subtask = subtasks.get(id);
                Epic epic = epics.get(subtask.getId());
                epic.removeSubtask(subtask);
                subtasks.remove(id);
                epic.updateProgress();
        } else if (epics.containsKey(id) ) {
                Epic epic = epics.get(id);
                epic.cleanSubtasks();
                epics.remove(id);
            }
    }

        @Override
        public ArrayList<SubTask> getSubtaskByEpic(Epic epic) { // Получение подзадач определенного эпика
        return epic.getSubtasks();
        }

        @Override
        public void updateTask(Task task, Task taskNew) {  // Обновление обычной задачи
        tasks.put(task.getId(), taskNew);
        }

        @Override
        public void updateSubTask(SubTask subTask, SubTask subTaskNew) { // Обновление подзадачи
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

    public void printAllTasks (TaskManager manager){
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtaskByEpic(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
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
