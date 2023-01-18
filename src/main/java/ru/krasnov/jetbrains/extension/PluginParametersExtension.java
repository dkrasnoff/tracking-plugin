package ru.krasnov.jetbrains.extension;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;

/**
 * Extension which holds all parameters for tracking plugin
 */
public abstract class PluginParametersExtension {

    @Nested
    public abstract ResultsCollectorServiceParameters getResultsCollectorService();

    public void resultsCollectorService(Action<? super ResultsCollectorServiceParameters> action) {
        action.execute(getResultsCollectorService());
    }

}
