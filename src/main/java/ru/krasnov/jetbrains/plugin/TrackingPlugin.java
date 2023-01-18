package ru.krasnov.jetbrains.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.invocation.BuildInvocationDetails;
import org.gradle.build.event.BuildEventsListenerRegistry;
import ru.krasnov.jetbrains.configuration.HttpConfiguration;
import ru.krasnov.jetbrains.configuration.ObjectMapperConfiguration;
import ru.krasnov.jetbrains.dispatcher.HttpDispatcher;
import ru.krasnov.jetbrains.listener.BuildFinishListener;
import ru.krasnov.jetbrains.listener.ExecutedTaskTrackingService;
import ru.krasnov.jetbrains.model.BuildTracker;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TrackingPlugin implements Plugin<Project> {

    public static final String EXECUTED_TASK_TRACKING_SERVICE_NAME = "executedTaskTrackingService";
    private final BuildEventsListenerRegistry buildEventsListenerRegistry;
    private final BuildInvocationDetails buildInvocationDetails;

    @Inject
    public TrackingPlugin(BuildEventsListenerRegistry buildEventsListenerRegistry,
                          BuildInvocationDetails buildInvocationDetails) {
        this.buildEventsListenerRegistry = buildEventsListenerRegistry;
        this.buildInvocationDetails = buildInvocationDetails;
    }

    @Override
    public void apply(Project project) {
        final var startParameter = project.getGradle().getStartParameter();
        final var buildTracker = new BuildTracker(startParameter.getTaskNames(), getBuildStartTimestamp());
        addExecutedTaskTrackingServiceListener(project, buildTracker);
        addBuildFinishedListener(project, buildTracker);
    }

    private LocalDateTime getBuildStartTimestamp() {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(buildInvocationDetails.getBuildStartedTime()),
                ZoneOffset.systemDefault());
    }

    private void addExecutedTaskTrackingServiceListener(Project project, BuildTracker buildTracker) {
        final var customService =
                project.getGradle()
                        .getSharedServices()
                        .registerIfAbsent(EXECUTED_TASK_TRACKING_SERVICE_NAME, ExecutedTaskTrackingService.class,
                                (spec) -> {
                                    spec.getParameters().getBuildTracker().set(buildTracker);
                                });

        buildEventsListenerRegistry.onTaskCompletion(customService);
    }

    private void addBuildFinishedListener(Project project, BuildTracker buildTracker) {
        project.getGradle().addBuildListener(
                new BuildFinishListener(
                        buildTracker,
                        new HttpDispatcher(
                                ObjectMapperConfiguration.getDefaultObjectMapper(),
                                HttpConfiguration.getDefaultHttpClient()
                        )));
    }

}
