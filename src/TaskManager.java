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

    public void getAllTasks() { // Метод вывода на экран всех задач
        System.out.println(tasks);
        System.out.println(epics);
        System.out.println(subtasks);
    }

    public void deleteAllTasks() { //Удаление всех задач
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public Object getTaskById(int id) { // Получение любой задачи по ID
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
        return epic.getSubtasks(epic);
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
        return "TaskManager{" + "\n" +
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