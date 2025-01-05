package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description, Progress.NEW);
        this.subTasks = new ArrayList<>();

    }
    public void addSubtask(SubTask subtask) { // Метод добавление подзадачи
        subTasks.add(subtask);
        updateProgress();
    }

    public void removeSubtask(SubTask subtask) {
        subTasks.remove(subtask);
    }

    public void cleanSubtasks() {
        subTasks.clear();
    }

    public ArrayList<SubTask> getSubtasks() {
        return subTasks;
    }
    public void updateProgress() { // Метод обновления эпика
        if (subTasks.isEmpty()) {
            setProgress(Progress.NEW);
            return;
        }
        boolean allDone = false;
        boolean inProgress = false;

        for (SubTask subtask : subTasks) {
            if (subtask.getProgress() == Progress.DONE) {
                allDone = true;
            }
                if (subtask.getProgress() == Progress.IN_PROGRESS) {
                    inProgress = true;
                }

        }
        if (allDone) {
            setProgress(Progress.DONE);
        } else if (inProgress) {
            setProgress(Progress.IN_PROGRESS);
        } else {
            setProgress(Progress.NEW);
        }
    }
}
