package ru.krasnov.jetbrains.listener;

import org.gradle.api.provider.Property;
import org.gradle.tooling.events.FinishEvent;
import org.gradle.tooling.events.OperationDescriptor;
import org.gradle.tooling.events.OperationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.krasnov.jetbrains.model.BuildTracker;
import ru.krasnov.jetbrains.model.ExecutedTask;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ExecutedTaskTrackingServiceTest {

    private static final String DEFAULT_EXECUTED_TASK_NAME = "executed task name";
    public static final long DEFAULT_TASK_START_TIME_MILLIS = LocalDateTime.of(2023, 1, 18, 17, 0, 0)
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli();
    public static final long DEFAULT_TASK_END_TIME_MILLIS = LocalDateTime.of(2023, 1, 18, 17, 0, 1)
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli();

    private ExecutedTaskTrackingService uut;

    @Mock
    private FinishEvent finishEvent;
    @Mock
    private OperationDescriptor operationDescriptor;
    @Mock
    private OperationResult operationResult;
    @Mock
    private ExecutedTaskTrackingService.ExecutedTaskTrackingServiceParameters parameters;
    @Mock
    private Property<BuildTracker> buildTrackerProperty;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        uut = new ExecutedTaskTrackingService() {
            @Override
            public ExecutedTaskTrackingServiceParameters getParameters() {
                return parameters;
            }
        };

        when(parameters.getBuildTracker()).thenReturn(buildTrackerProperty);
        when(finishEvent.getDescriptor()).thenReturn(operationDescriptor);
        when(finishEvent.getResult()).thenReturn(operationResult);
    }

    @Test
    void onFinishSuccessfulTest() {
        final var buildTracker = new BuildTracker(List.of("one task"), LocalDateTime.MIN);

        when(buildTrackerProperty.isPresent()).thenReturn(true);
        when(buildTrackerProperty.get()).thenReturn(buildTracker);
        when(operationDescriptor.getName()).thenReturn(DEFAULT_EXECUTED_TASK_NAME);
        when(operationResult.getStartTime()).thenReturn(DEFAULT_TASK_START_TIME_MILLIS);
        when(operationResult.getEndTime()).thenReturn(DEFAULT_TASK_END_TIME_MILLIS);

        // check state before test
        Assertions.assertSame(0, buildTracker.getExecutedTasks().size());

        uut.onFinish(finishEvent);

        // checking state after test
        Assertions.assertSame(1, buildTracker.getExecutedTasks().size());
        Assertions.assertEquals(
                new ExecutedTask(DEFAULT_EXECUTED_TASK_NAME,
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(DEFAULT_TASK_START_TIME_MILLIS), ZoneOffset.UTC),
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(DEFAULT_TASK_END_TIME_MILLIS), ZoneOffset.UTC)),
                buildTracker.getExecutedTasks().get(0));

        verify(parameters).getBuildTracker();
        verify(buildTrackerProperty).isPresent();
        verify(buildTrackerProperty).get();
        verify(finishEvent).getDescriptor();
        verify(finishEvent).getResult();
        verify(operationDescriptor).getName();
        verify(operationResult).getStartTime();
        verify(operationResult).getEndTime();
        verifyNoMoreInteractions(finishEvent, parameters, buildTrackerProperty, finishEvent, operationDescriptor, operationResult);
    }

    @Test
    void onFinishIfThereIsNoBuildTrackerWasGivenTest() {
        when(buildTrackerProperty.isPresent()).thenReturn(false);

        uut.onFinish(finishEvent);

        verify(parameters).getBuildTracker();
        verify(buildTrackerProperty).isPresent();
        verifyNoMoreInteractions(finishEvent, parameters, buildTrackerProperty);
    }
}
