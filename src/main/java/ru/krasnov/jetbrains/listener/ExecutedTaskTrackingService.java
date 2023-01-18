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

/**
 * This listener is responsible for catching all executed tasks and collecting their data
 */
@AllArgsConstructor
public abstract class ExecutedTaskTrackingService
        implements BuildService<ExecutedTaskTrackingService.ExecutedTaskTrackingServiceParameters>, OperationCompletionListener {

    public interface ExecutedTaskTrackingServiceParameters extends BuildServiceParameters {
        Property<BuildTracker> getBuildTracker();
    }

    @Override
    public void onFinish(FinishEvent event) {
        final var buildTrackerProperty = getParameters().getBuildTracker();
        if (buildTrackerProperty.isPresent()) {
            final var eventResult = event.getResult();
            buildTrackerProperty
                    .get()
                    .addNewExecutedTask(
                            new ExecutedTask(
                                    event.getDescriptor().getName(),
                                    LocalDateTime.ofInstant(
                                            Instant.ofEpochMilli(eventResult.getStartTime()),
                                            ZoneOffset.UTC),
                                    LocalDateTime.ofInstant(
                                            Instant.ofEpochMilli(eventResult.getEndTime()),
                                            ZoneOffset.UTC)
                            ));
        }
    }
}
