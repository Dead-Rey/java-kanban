package controllers;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int idCounter = 1;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subtasks;

    public TaskManager() { // Конструктор для всех типов
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public void addTask(Task task) { // Добавление задачи + присваивание уникального ID
        task.setId(idCounter);
        tasks.put(idCounter++, task);

    }

   public void addEpic(Epic epic) { // Добавление эпика + присваивание уникального ID
       epic.setId(idCounter);
       epics.put(idCounter++, epic);
   }

    public void addSubtask(SubTask subtask) { // Добавление подзадачи + присваивание уникального ID
        subtask.setId(idCounter);
        subtasks.put(idCounter++, subtask);
    }

    public ArrayList<Task> getTasks() {  // Метод возврата задач
        return new ArrayList<>(tasks.values());
    }
    public ArrayList<Epic> getEpics() { //Метод возврата эпиков
        return new ArrayList<>(epics.values());
    }
    public ArrayList<SubTask> getSubtasks() { //Метод возврата подзадач
        return new ArrayList<>(subtasks.values());
    }

    public void deleteTask() { // Удаление всех задач
        tasks.clear();
    }
    public void deleteSubtask() {  // Удаление всех подзадач
        for (Epic epic : epics.values()) {
            epic.cleanSubtasks(); // Удаление всех подзадач из эпиков
            epic.updateProgress(); // Обновление прогресса эпиков
        }
        subtasks.clear();
    }
    public void deleteEpic() { // Удаление эпиков и подзадач как следствие
        epics.clear();
        subtasks.clear();
    }

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

    public void deleteTaskById(int id) { // Удаление любой задачи по ID
        if (tasks.containsKey(id)){
            tasks.remove(id);
        } else
            if (subtasks.containsKey(id)) {
            subtasks.remove(id);
        } else if (epics.containsKey(id) ) {
                epics.remove(id);
            } else {
                System.out.println("Задача не найдена");
            }
    }

        public ArrayList<SubTask> getSubtaskByEpic(Epic epic) { // Получение подзадач определенного эпика
        return epic.getSubtasks();
        }

        public void updateTask(Task task, Task taskNew) { // Обновление обычной задачи
        tasks.put(task.getId(), taskNew);
        }

        public void updateSubTask(SubTask subTask, SubTask subTaskNew) { // Обновление подзадачи
        subtasks.put(subTask.getId(), subTaskNew);
        Epic epic = epics.get(subTask.getEpicId());
        epic.addSubtask(subTaskNew);
        epics.put(epic.getId(), epic);
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
/* Комментарий для ревьювера:
 Здравствуй! С Новым Годом !!!!
 Честно скажу, задача показалась не простой, и очень много вопросов возникало в процессе решения.
 Если есть возможность, как то улучшить программу, то буду ждать комментариев.
 Имею в виду, что не только для зачета, а с точки зрения правильности написания, коммерческой разработки, итд.
 Благодарю за уделенное время, и еще раз с праздником !
 */