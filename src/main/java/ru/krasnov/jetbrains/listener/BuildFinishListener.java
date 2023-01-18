package ru.krasnov.jetbrains.listener;

import lombok.AllArgsConstructor;
import org.gradle.BuildAdapter;
import org.gradle.BuildResult;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.provider.Provider;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceRegistration;
import org.gradle.api.services.BuildServiceRegistry;
import ru.krasnov.jetbrains.dispatcher.HttpDispatcher;
import ru.krasnov.jetbrains.model.BuildTracker;

import java.time.LocalDateTime;
import java.util.Optional;

import static ru.krasnov.jetbrains.plugin.TrackingPlugin.EXECUTED_TASK_TRACKING_SERVICE_NAME;

/**
 * This listener responsible for catching the end of the hole build and processing all collected during build data.
 */
@AllArgsConstructor
public class BuildFinishListener extends BuildAdapter {

    private final BuildTracker defaultBuildTracker;
    private final HttpDispatcher httpDispatcher;

    @Override
    public void buildFinished(BuildResult result) {
        final var buildTracker = getBuildTrackerFromSharedServices(result);
        buildTracker.setBuildEndTimestamp(LocalDateTime.now());
        httpDispatcher.sendExecutedBuildResult(buildTracker);
    }

    private BuildTracker getBuildTrackerFromSharedServices(BuildResult result) {
        return ((Optional<ExecutedTaskTrackingService>)
                Optional.ofNullable(result.getGradle())
                        .map(Gradle::getSharedServices)
                        .map(BuildServiceRegistry::getRegistrations)
                        .map(reg -> reg.getByName(EXECUTED_TASK_TRACKING_SERVICE_NAME))
                        .map(BuildServiceRegistration::getService)
                        .map(Provider::get))
                .map(BuildService::getParameters)
                .map(ExecutedTaskTrackingService.ExecutedTaskTrackingServiceParameters::getBuildTracker)
                .map(Provider::get)
                .orElse(this.defaultBuildTracker);
    }
}
