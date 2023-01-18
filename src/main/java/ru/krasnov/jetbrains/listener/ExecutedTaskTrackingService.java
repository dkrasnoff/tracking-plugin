package ru.krasnov.jetbrains.listener;

import lombok.AllArgsConstructor;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.gradle.tooling.events.FinishEvent;
import org.gradle.tooling.events.OperationCompletionListener;
import ru.krasnov.jetbrains.model.BuildTracker;
import ru.krasnov.jetbrains.model.ExecutedTask;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@AllArgsConstructor
public abstract class ExecutedTaskTrackingService
        implements BuildService<ExecutedTaskTrackingService.ExecutedTaskTrackingServiceParameters>, OperationCompletionListener {

    public interface ExecutedTaskTrackingServiceParameters extends BuildServiceParameters {
        Property<BuildTracker> getBuildTracker();
    }

    @Override
    public void onFinish(FinishEvent event) {
        if (getParameters().getBuildTracker().isPresent()) {
            getParameters().getBuildTracker()
                    .get()
                    .addNewExecutedTask(
                            new ExecutedTask(
                                    event.getDescriptor().getName(),
                                    LocalDateTime.ofInstant(
                                            Instant.ofEpochMilli(event.getResult().getStartTime()),
                                            ZoneOffset.systemDefault()),
                                    LocalDateTime.ofInstant(
                                            Instant.ofEpochMilli(event.getResult().getEndTime()),
                                            ZoneOffset.systemDefault())
                            ));
        }
    }
}
