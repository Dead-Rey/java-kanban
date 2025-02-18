package main.fileManagers;

import main.java.controllers.InMemoryTaskManager;
import main.java.controllers.TaskManager;
import main.java.controllers.model.Epic;
import main.java.controllers.model.Progress;
import main.java.controllers.model.SubTask;
import main.java.controllers.model.Task;

import java.io.*;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = new File(file.getAbsolutePath());
    }

    public void save() {
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
            System.out.println("Ошибка сохранения в файл: " + e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
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
            System.out.println("Ошибка загрузки из файла: " + e.getMessage());
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
