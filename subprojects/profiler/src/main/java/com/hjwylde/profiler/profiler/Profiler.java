package com.hjwylde.profiler.profiler;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Stopwatch;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.0
 */
public final class Profiler {

    private final ProfilerOptions options;

    public Profiler(ProfilerOptions options) {
        this.options = checkNotNull(options, "options cannot be null");
    }

    public Result profile(Task task) throws ClassNotFoundException {
        Result.Builder builder = Result.builder(task);

        JUnitCore core = new JUnitCore();
        Request request = Request.method(Class.forName(task.getClazz()), task.getMethod());

        for (int i = 0; i < options.getWarmupRuns(); i++) {
            core.run(request);
        }

        Stopwatch stopwatch = Stopwatch.createUnstarted();
        List<Long> runs = new ArrayList<>();
        for (int i = 0; i < options.getRuns(); i++) {
            stopwatch.start();

            org.junit.runner.Result result = core.run(request);

            stopwatch.stop();

            runs.add(stopwatch.elapsed(TimeUnit.MILLISECONDS));

            stopwatch.reset();
        }

        builder.setRuns(runs);

        return builder.build();
    }
}
