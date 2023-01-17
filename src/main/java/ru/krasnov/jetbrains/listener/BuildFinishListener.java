package ru.krasnov.jetbrains.listener;

import lombok.AllArgsConstructor;
import org.gradle.BuildAdapter;
import org.gradle.BuildResult;
import ru.krasnov.jetbrains.model.BuildTracker;

import java.time.LocalDateTime;

@AllArgsConstructor
public class BuildFinishListener extends BuildAdapter  {

    private final BuildTracker buildTracker;

    @Override
    public void buildFinished(BuildResult result) {
        buildTracker.setBuildEndTimestamp(LocalDateTime.now());
        // TODO(d.krasnov): change to post sender
        System.out.println("BUILD TRACKER FINISHED HASH: " + buildTracker.hashCode());
        System.out.println("BUILD FINISHED: " + buildTracker);
    }
}
