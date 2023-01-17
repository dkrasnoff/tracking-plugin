package ru.krasnov.jetbrains.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.concurrent.Immutable;
import java.time.LocalDateTime;

@Immutable
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ExecutedTask extends Task {
    private final LocalDateTime executionStartTimestamp;
    private final LocalDateTime executionEndTimestamp;

    public ExecutedTask(String name,
                        LocalDateTime executionStartTimestamp,
                        LocalDateTime executionEndTimestamp) {
        super(name);
        this.executionStartTimestamp = executionStartTimestamp;
        this.executionEndTimestamp = executionEndTimestamp;
    }
}
