package ru.krasnov.jetbrains.extension;

import org.gradle.api.provider.Property;

/**
 * This interface contains all properties for external service, which collects build results from this plugin
 */
public interface ResultsCollectorServiceParameters {
    Property<String> getUrl();
}
