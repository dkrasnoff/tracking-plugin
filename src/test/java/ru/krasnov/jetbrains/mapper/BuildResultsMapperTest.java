package ru.krasnov.jetbrains.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.krasnov.jetbrains.TestUtils;
import ru.krasnov.jetbrains.dto.api.BuildResultsRequest;
import ru.krasnov.jetbrains.model.BuildTracker;
import ru.krasnov.jetbrains.model.ExecutedTask;

import java.time.LocalDateTime;
import java.util.List;

public class BuildResultsMapperTest {

    private BuildResultsMapper uut = Mappers.getMapper(BuildResultsMapper.class);

    @Test
    public void mapTest() throws IllegalAccessException {
        final var buildTracker = createFullfilledBuildTracker();

        final var result = uut.mapFromModel(buildTracker);

        TestUtils.checkThatAllFieldsAreFilled(result);

        final var expectedExecutedTasks =
                List.of(createExecutedTaskApiDto("ExecutedTask 1"), createExecutedTaskApiDto("ExecutedTask 2"));
        Assertions.assertEquals(expectedExecutedTasks, result.getExecutedTasks());

        final var expectedRequestedTasks = List.of("Task1", "Task2");
        Assertions.assertEquals(expectedRequestedTasks, result.getRequestedTaskNames());
    }

    private BuildTracker createFullfilledBuildTracker() {
        final var buildTracker = new BuildTracker(List.of("Task1", "Task2"), LocalDateTime.MIN);
        buildTracker.addNewExecutedTask(createExecutedTask("ExecutedTask 1"));
        buildTracker.addNewExecutedTask(createExecutedTask("ExecutedTask 2"));
        buildTracker.setBuildEndTimestamp(LocalDateTime.MAX);
        return buildTracker;
    }

    private static ExecutedTask createExecutedTask(String name) {
        return new ExecutedTask(
                name, LocalDateTime.MIN.plusDays(1), LocalDateTime.MAX.minusDays(1));
    }

    private static BuildResultsRequest.ExecutedTask createExecutedTaskApiDto(String name) {
        return new BuildResultsRequest.ExecutedTask(
                name, LocalDateTime.MIN.plusDays(1), LocalDateTime.MAX.minusDays(1));
    }
}
