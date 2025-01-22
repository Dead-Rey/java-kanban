package controllers;

import model.Task;

import java.util.List;

public final class Managers {

    static TaskManager InMemoryTaskManager = new InMemoryTaskManager();

    public static TaskManager getDefault(){
        return InMemoryTaskManager;
    }

    public static List<Task> getDefaultHistory() {
        return InMemoryTaskManager.getHistory();
    }
}


