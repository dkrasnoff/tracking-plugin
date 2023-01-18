package ru.krasnov.jetbrains.extension;

import org.gradle.api.provider.Property;

public interface ResultsCollectorServiceParameters {
    Property<String> getUrl();
}
