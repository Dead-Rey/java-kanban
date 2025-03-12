package main.fileManagers;

import main.exception.ManagerSaveException;
import main.java.controllers.InMemoryTaskManager;
import main.java.controllers.model.Epic;
import main.java.controllers.model.Progress;
import main.java.controllers.model.SubTask;
import main.java.controllers.model.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;


public class FileBackedTaskManager extends InMemoryTaskManager {

   public static void main(String[] args) {
        File file = new File("test.csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Покормить кота", "Насыпать корм в миску", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Сделать зарядку", "Зарядка в 9:00", Progress.NEW,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));

       manager.addTask(task1);
       manager.addTask(task2);

        Epic epic1 = new Epic("Сделать ФЗ", "Решить задачу");
        Epic epic2 = new Epic("Отметить новый год", "Найти настроение");

       manager.addEpic(epic1);
       manager.addEpic(epic2);

       SubTask subTask1 = new SubTask("Разобраться с задачей",
               "Понять что нужно сделать",Progress.NEW, epic1.getId(),
               Duration.ofMinutes(30), LocalDateTime.now().plusHours(2));
       SubTask subTask2 = new SubTask("Написать код",
               "Применить знания полученные при обучении", Progress.NEW, epic1.getId(),
               Duration.ofMinutes(30), LocalDateTime.now().plusHours(3));
       SubTask subTask3 = new SubTask("Купить продукты",
               "Приготовить салаты", Progress.NEW, epic2.getId(),
               Duration.ofMinutes(30), LocalDateTime.now().plusHours(4));

       manager.addSubtask(subTask1);
       manager.addSubtask(subTask2);
       manager.addSubtask(subTask3);
       epic1.addSubtask(subTask1);
       epic1.addSubtask(subTask2);
       epic2.addSubtask(subTask3);
       manager.deleteTaskById(epic2.getId());


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
            writer.write("id,type,name,status,description,epic,duration,startTime");
            writer.newLine();

            // Запись задач
            getTasks().stream()
                    .map(Task::toString)
                    .forEach(taskString -> {
                        try {
                            writer.write(taskString);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });

            // Запись эпиков
            getEpics().stream()
                    .map(Epic::toString)
                    .forEach(epicString -> {
                        try {
                            writer.write(epicString);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });

            // Запись подзадач
            getSubtasks().stream()
                    .map(SubTask::toString)
                    .forEach(subtaskString -> {
                        try {
                            writer.write(subtaskString);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл: " + e.getMessage());
        }
    }


    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        setIdCounter(1);

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
                        long durationsInMinutes = Long.parseLong(parts[5]);
                        Duration duration = Duration.ofMinutes(durationsInMinutes);
                        LocalDateTime startTime = LocalDateTime.parse(parts[6]);
                        Task task = new Task(name, description, progress,duration, startTime);
                        manager.addTask(task);
                        break;
                    case "EPIC":
                        Epic epic = new Epic(name, description);
                        manager.addEpic(epic);
                        break;
                    case "SUBTASK":
                        long durationsInMinutes2 = Long.parseLong(parts[6]);
                        Duration duration2 = Duration.ofMinutes(durationsInMinutes2);
                        LocalDateTime startTime2 = LocalDateTime.parse(parts[7]);
                        int epicId = Integer.parseInt(parts[5]); // Получаем ID эпика
                        SubTask subtask = new SubTask(name, description, progress, epicId, duration2, startTime2);
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
