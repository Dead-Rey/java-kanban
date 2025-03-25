package main.exception;

public class TaskOverlapException extends Exception {

    public TaskOverlapException() {
        super("Задача пересекается с существующей задачей");
    }

    public TaskOverlapException(String message) {
        super(message);
    }

    public TaskOverlapException(String message, Throwable cause) {
        super(message, cause);
    }
}
