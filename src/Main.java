import controllers.*;
import model.Epic;
import model.Progress;
import model.SubTask;
import model.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();


        Task task1 = new Task("Покормить кота", "Насыпать корм в миску", Progress.NEW);
        Task task2 = new Task("Сделать зарядку", "Зарядка в 9:00", Progress.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2); //Добавление обычных задач в менеджер

        Epic epic1 = new Epic("Сделать ФЗ", "Решить задачу");
        Epic epic2 = new Epic("Отметить новый год", "Найти настроение");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2); //Добавление эпиков в менеджер

        SubTask subTask1 = new SubTask("Разобраться с задачей",
                "Понять что нужно сделать",Progress.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("Написать код",
                "Применить знания полученные при обучении", Progress.NEW, epic1.getId());
        SubTask subTask3 = new SubTask("Купить продукты",
                "Всё необходимое к столу", Progress.NEW, epic2.getId());
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);
        taskManager.addSubtask(subTask3); //Добавление подзадач в менеджер

        epic1.addSubtask(subTask1);
        epic1.addSubtask(subTask2);
        epic2.addSubtask(subTask3); //Добавление подзадач в эпик

        Task task1New = new Task("Покормить кота", "Насыпать корм в миску", Progress.DONE);
        SubTask subTask1New = new SubTask("Разобраться с задачей",
                "Понять что нужно сделать",Progress.DONE,epic1.getId()); // Обновление задачи
        SubTask subTask2New = new SubTask("Написать код",
                "Применить знания полученные при обучении", Progress.DONE,epic1.getId()); // Обновление подзадачи
        printAllTasks(taskManager);
        taskManager.deleteTaskById(2); // Удаление задачи по ее ID
        taskManager.updateTask(task1,task1New); // Обновление задачи
        taskManager.updateSubTask(subTask1, subTask1New); // Обновление подзадачи
        taskManager.updateSubTask(subTask2, subTask2New);
        taskManager.getTaskById(1);
        taskManager.getTaskById(4);
        printAllTasks(taskManager);
    }

    public static void printAllTasks(TaskManager manager) {
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

}
