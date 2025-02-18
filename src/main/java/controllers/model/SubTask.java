package main.java.controllers.model;

public class SubTask extends Task {

    private final int epicId;

    public SubTask(String name, String description, Progress progress, int epicId) {
        super(name, description, progress);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

}
