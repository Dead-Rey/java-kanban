package main.java.controllers.model;

import main.fileManagers.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private final int epicId;

    public SubTask(String name, String description, Progress progress,int epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, progress, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }


    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s", getId(), TaskType.SUBTASK, getName(), getProgress(), getDescription(),
                getEpicId(),getDurationInMinutes(),getStartTime());
    }


}
