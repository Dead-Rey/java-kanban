import java.util.Objects;

public class Task {
    private int id;
    private final String name;
    private final String description;
    private  Progress progress;


    public Task(String name, String description, Progress progress) {
        this.progress = progress;
        this.name = name;
        this.description = description;
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
        return "ID " + this.getClass().getName() +
                "{name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", progress=" + progress +
                '}' + "\n" ;
    }
}



