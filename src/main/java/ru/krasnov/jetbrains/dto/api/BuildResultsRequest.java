package ru.krasnov.jetbrains.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This is request API dto for sending requests to result collecting service
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BuildResultsRequest {

    private List<String> requestedTaskNames;
    private LocalDateTime buildStartTimestamp;
    private LocalDateTime buildEndTimestamp;
    private List<ExecutedTask> executedTasks;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ExecutedTask {
        private String name;
        private LocalDateTime executionStartTimestamp;
        private LocalDateTime executionEndTimestamp;
    }
}
