package main.java.controllers.model;

import main.fileManagers.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    private int id;
    private final String name;
    private final String description;
    private Progress progress;
    private Duration duration;
    private LocalDateTime startTime;


    public Task(String name, String description, Progress progress, Duration duration, LocalDateTime startTime) {
        this.progress = progress;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    public long getDurationInMinutes() {
        return duration.toMinutes();
    }

    public void setDurationOfMinutes(long minutes) {
        this.duration = Duration.ofMinutes(minutes);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public Progress getProgress() {
        return progress;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && progress == task.progress;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, progress);
    }

    @Override
    public String toString() { // Переопределение метода для адекватной печати имени класса
        return String.format("%s,%s,%s,%s,%s,%s,%s", getId(), TaskType.TASK, getName(), getProgress(),
                getDescription(),getDurationInMinutes(),getStartTime());
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(Task o) {
        return this.startTime.compareTo(o.startTime);
    }
}



