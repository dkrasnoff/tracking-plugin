package ru.krasnov.jetbrains.extension;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;

public abstract class PluginParametersExtension {

    @Nested
    public abstract ResultsCollectorServiceParameters getResultsCollectorService();

    public void resultsCollectorService(Action<? super ResultsCollectorServiceParameters> action) {
        action.execute(getResultsCollectorService());
    }

}
