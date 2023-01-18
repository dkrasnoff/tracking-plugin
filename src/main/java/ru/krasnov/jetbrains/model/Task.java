package ru.krasnov.jetbrains.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.concurrent.Immutable;
import java.io.Serializable;

/**
 * Base model for all implementations of tasks
 */
@Immutable
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Task implements Serializable {
    private final String name;
}
