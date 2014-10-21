package com.hjwylde.whiley.evaluator.evaluator;

import com.hjwylde.profiler.profiler.Task;

import com.google.common.io.CharStreams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.0
 */
public final class Evaluator {

    private static final Logger logger = LoggerFactory.getLogger(Evaluator.class);

    private static final String TIMEOUT_EXECUTABLE = "timeout";
    private static final String PROFILER_EXECUTABLE =
            "/home/hjwylde/git/whiley-compiler-evaluator/subprojects/profiler/build/install/profiler/bin/profiler";

    public Result evaluate(List<Task> tasks) throws IOException, InterruptedException {
        logger.debug("evaluating {} tasks", tasks.size());

//        StringBuilder csv = new StringBuilder();
        for (Task task : tasks) {
            logger.debug("evaluating task {}", task);

            ProcessBuilder pb = new ProcessBuilder(TIMEOUT_EXECUTABLE, String.valueOf(10000),
                    PROFILER_EXECUTABLE, task.getClazz(), task.getMethod());

            final Process profiler = pb.start();

            profiler.waitFor();

            String output;

            if (profiler.exitValue() != 0) {
                logger.warn("{} exited with non-zero exit value: {}", task, profiler.exitValue());

                output = task + ",,,,,,";
            } else {
                output = CharStreams.toString(new InputStreamReader(profiler.getInputStream()));
            }

            logger.info(output);
        }

//        return Result.builder(tasks).setCsv(csv.toString()).build();
        return null;
    }
}
