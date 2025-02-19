package main.fileManagers;

import main.exception.ManagerSaveException;
import main.java.controllers.InMemoryTaskManager;
import main.java.controllers.model.Epic;
import main.java.controllers.model.Progress;
import main.java.controllers.model.SubTask;
import main.java.controllers.model.Task;

import java.io.*;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {

   public static void main(String[] args) {
        File file = new File("test.csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Покормить кота", "Насыпать корм в миску", Progress.NEW);
        Task task2 = new Task("Сделать зарядку", "Зарядка в 9:00", Progress.NEW);

       manager.addTask(task1);
       manager.addTask(task2);

        Epic epic1 = new Epic("Сделать ФЗ", "Решить задачу");
        Epic epic2 = new Epic("Отметить новый год", "Найти настроение");

       manager.addEpic(epic1);
       manager.addEpic(epic2);

       SubTask subTask1 = new SubTask("Разобраться с задачей",
               "Понять что нужно сделать",Progress.NEW, epic1.getId());
       SubTask subTask2 = new SubTask("Написать код",
               "Применить знания полученные при обучении", Progress.NEW, epic1.getId());
       SubTask subTask3 = new SubTask("Купить продукты",
               "Сдать работу", Progress.NEW, epic1.getId());

       manager.addSubtask(subTask1);
       manager.addSubtask(subTask2);
       manager.addSubtask(subTask3);

       manager.save();

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);

       if (manager.toString().equals(newManager.toString())) {
           System.out.println("Всё работает правильно");
      }
    }

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = new File(file.getAbsolutePath());
    }

    public void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            List<Task> tasks = getTasks();
            for (Task task : tasks) {
                writer.write(task.toString());
                writer.newLine();
            }

            List<Epic> epics = getEpics();
            for (Epic epic : epics) {
                writer.write(epic.toString());
                writer.newLine();
            }

            List<SubTask> subtasks = getSubtasks();
            for (SubTask subtask : subtasks) {
                writer.write(subtask.toString());
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        setIdCounter(1); // У меня тут вопрос. Без этого обновления счётчика ID, задачи записывались с повышенным ID
                        // Правильно ли я реализовал данное обновление?
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Пропускаем заголовок
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String type = parts[1];
                String name = parts[2];
                Progress progress = Progress.valueOf(parts[3]);
                String description = parts[4];

                switch (type) {
                    case "TASK":
                        Task task = new Task(name, description, progress);
                        manager.addTask(task);
                        break;
                    case "EPIC":
                        Epic epic = new Epic(name, description);
                        manager.addEpic(epic);
                        break;
                    case "SUBTASK":
                        int epicId = Integer.parseInt(parts[5]); // Получаем ID эпика
                        SubTask subtask = new SubTask(name, description, progress, epicId);
                        manager.addSubtask(subtask);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла: " + e.getMessage());
        }
        return manager;
    }




    // Переопределяем методы, изменяющие информацию, и примеряем save
    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(SubTask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask() {
        super.deleteTask();
        save();
    }

    @Override
    public void deleteEpic() {
        super.deleteEpic();
        save();
    }

    @Override
    public void deleteSubtask() {
        super.deleteSubtask();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }
}
