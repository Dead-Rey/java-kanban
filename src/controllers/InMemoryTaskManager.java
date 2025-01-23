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
    private final HistoryManager historyManager = Managers.getDefaultHistory();

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
    public Task getTaskById(int id) {// Получение любой задачи по ID
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



    @Override
    public String toString() {
        return "controllers.TaskManager{" + "\n" +
                "Tasks=" + tasks + "\n" +
                "Epics=" + epics + "\n" +
                "Subtasks=" + subtasks +
                '}';
    }
}
