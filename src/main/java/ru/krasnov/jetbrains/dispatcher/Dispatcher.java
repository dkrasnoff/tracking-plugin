package ru.krasnov.jetbrains.dispatcher;

import ru.krasnov.jetbrains.model.BuildTracker;

/**
 * Interface provides base operations for dispatching tracking results
 */
public interface Dispatcher {

    void sendExecutedBuildResult(BuildTracker buildTracker);
}
