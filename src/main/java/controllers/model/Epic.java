package main.java.controllers.model;

import main.fileManagers.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<SubTask> subTasks;
    private Duration duration;

    public Epic(String name, String description) {
        super(name, description, Progress.NEW, Duration.ZERO, null);
        this.subTasks = new ArrayList<>();
        this.duration = Duration.ZERO;

    }

    private void calculateDuration() {
        if (subTasks.isEmpty()) {
            return;
        }

        // Суммируем продолжительности всех подзадач
        duration = subTasks.stream()
                .map(SubTask::getDuration) // Получаем продолжительность каждой подзадачи
                .reduce(Duration.ZERO, Duration::plus); // Суммируем продолжительности

        setDurationOfMinutes(duration.toMinutes()); // Устанавливаем общую продолжительность

        // Находим самое раннее время начала
        LocalDateTime earliestStartTime = subTasks.stream()
                .map(SubTask::getStartTime) // Получаем время начала каждой подзадачи
                .filter(Objects::nonNull) // Фильтруем null значения
                .min(LocalDateTime::compareTo) // Находим самое раннее время начала
                .orElse(null); // Если нет стартового времени, возвращаем null

        setStartTime(earliestStartTime); // Устанавливаем самое раннее время начала
    }


    public void addSubtask(SubTask subtask) { // Метод добавление подзадачи
        subTasks.add(subtask);
        updateProgress();
        calculateDuration();
    }

    public void removeSubtask(SubTask subtask) {
        subTasks.remove(subtask);
        updateProgress();
        calculateDuration();
    }

    public void cleanSubtasks() {
        subTasks.clear();
        updateProgress();
        calculateDuration();
    }

    public ArrayList<SubTask> getSubtasks() {
        return subTasks;
    }

    public void updateProgress() { // Метод обновления эпика
        if (subTasks.isEmpty()) {
            setProgress(Progress.NEW);
            return;
        }

        boolean allDone = subTasks.stream()
                .allMatch(subtask -> subtask.getProgress() == Progress.DONE); // Проверяем, все ли подзадачи выполнены

        boolean inProgress = subTasks.stream()
                .anyMatch(subtask -> subtask.getProgress() == Progress.IN_PROGRESS); // Проверяем, есть ли подзадачи в процессе выполнения

        boolean hasNew = subTasks.stream()
                .anyMatch(subtask -> subtask.getProgress() == Progress.NEW);

        if (allDone) {
            setProgress(Progress.DONE);
        } else if (inProgress || (hasNew && subTasks.stream().anyMatch(subtask ->
                subtask.getProgress() == Progress.DONE))) {
            setProgress(Progress.IN_PROGRESS);
        } else {
            setProgress(Progress.NEW);
        }
    }


    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s", getId(), TaskType.EPIC, getName(),getProgress(),getDescription()
                ,getDurationInMinutes(),getStartTime());
    }
}
