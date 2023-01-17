package ru.krasnov.jetbrains.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ThreadSafe
@Getter
public class BuildTracker implements Serializable {

    private final List<String> requestedTaskNames;
    private final LocalDateTime buildStartTimestamp;
    private volatile LocalDateTime buildEndTimestamp;
    private final List<ExecutedTask> executedTasks;

    public BuildTracker(List<String> requestedTaskNames,
                        LocalDateTime buildStartTimestamp) {
        System.out.println("CREATING NEW BuildTracker");
        this.requestedTaskNames = List.copyOf(requestedTaskNames);
        this.buildStartTimestamp = buildStartTimestamp;
        this.executedTasks = new CopyOnWriteArrayList<>();
    }

    public void setBuildEndTimestamp(LocalDateTime newBuildTimestamp) {
        buildEndTimestamp = newBuildTimestamp;
    }

    public void addNewExecutedTask(@NonNull ExecutedTask task) {
        executedTasks.add(task);
    }

    public List<ExecutedTask> getExecutedTasks() {
        return List.copyOf(executedTasks);
    }
}
