package ru.krasnov.jetbrains.listener;

import org.gradle.BuildResult;
import org.gradle.api.NamedDomainObjectSet;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildServiceRegistration;
import org.gradle.api.services.BuildServiceRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.krasnov.jetbrains.dispatcher.HttpDispatcher;
import ru.krasnov.jetbrains.model.BuildTracker;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BuildFinishListenerTest {

    private BuildFinishListener uut;

    @Mock
    private BuildTracker defaultBuildTracker;
    @Mock
    private HttpDispatcher httpDispatcher;

    @Mock
    private BuildTracker sharedBuildTracker;
    @Mock
    private BuildResult buildResult;
    @Mock
    private Gradle gradle;
    @Mock
    private BuildServiceRegistry buildServiceRegistry;
    @Mock
    private NamedDomainObjectSet<BuildServiceRegistration<?, ?>> namedDomainObjectSet;
    @Mock
    private BuildServiceRegistration buildServiceRegistration;
    @Mock
    private Property<ExecutedTaskTrackingService> executedTaskTrackingServiceProperty;
    @Mock
    private ExecutedTaskTrackingService executedTaskTrackingService;
    @Mock
    private ExecutedTaskTrackingService.ExecutedTaskTrackingServiceParameters parameters;
    @Mock
    private Property<BuildTracker> buildTrackerProperty;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        uut = new BuildFinishListener(defaultBuildTracker, httpDispatcher);

        doNothing().when(httpDispatcher).sendExecutedBuildResult(any());
    }

    @Test
    public void buildFinishedWithSharedBuildTrackerTest() {

        when(buildResult.getGradle()).thenReturn(gradle);
        when(gradle.getSharedServices()).thenReturn(buildServiceRegistry);
        when(buildServiceRegistry.getRegistrations()).thenReturn(namedDomainObjectSet);
        when(namedDomainObjectSet.getByName(eq("executedTaskTrackingService"))).thenReturn(buildServiceRegistration);
        when(buildServiceRegistration.getService()).thenReturn(executedTaskTrackingServiceProperty);
        when(executedTaskTrackingServiceProperty.get()).thenReturn(executedTaskTrackingService);
        when(executedTaskTrackingService.getParameters()).thenReturn(parameters);
        when(parameters.getBuildTracker()).thenReturn(buildTrackerProperty);
        when(buildTrackerProperty.get()).thenReturn(sharedBuildTracker);

        uut.buildFinished(buildResult);

        verify(sharedBuildTracker).setBuildEndTimestamp(any());
        verify(httpDispatcher).sendExecutedBuildResult(sharedBuildTracker);
    }

    @Test
    public void buildFinishedWithDefaultBuildTrackerTest() {
        uut.buildFinished(buildResult);
        verify(defaultBuildTracker).setBuildEndTimestamp(any());
        verify(httpDispatcher).sendExecutedBuildResult(defaultBuildTracker);
    }
}
