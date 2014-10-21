package com.hjwylde.whiley.evaluator;

import com.hjwylde.profiler.profiler.Task;
import com.hjwylde.profiler.util.ExitCode;
import com.hjwylde.whiley.evaluator.evaluator.Evaluator;
import com.hjwylde.whiley.evaluator.evaluator.Result;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.0
 */
public final class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static List<Task> generateTasks() throws ClassNotFoundException {
        List<Task> tasks = new ArrayList<>();

        Class<?> validTests = Class.forName("wycs.testing.tests.ValidTests");
        for (Method method : validTests.getMethods()) {
            if (method.getAnnotation(Test.class) == null) {
                continue;
            }
            if (method.getAnnotation(Ignore.class) != null) {
                continue;
            }

            tasks.add(new Task(validTests.getName(), method.getName()));
        }

        Class<?> invalidTests = Class.forName("wycs.testing.tests.InvalidTests");
        for (Method method : invalidTests.getMethods()) {
            if (method.getAnnotation(Test.class) == null) {
                continue;
            }
            if (method.getAnnotation(Ignore.class) != null) {
                continue;
            }

            tasks.add(new Task(invalidTests.getName(), method.getName()));
        }

        return tasks;
    }


    /**
     * Main.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        LogManager.getRootLogger().setLevel(Level.DEBUG);

        try {
            System.exit(new Main().run(generateTasks()));
        } catch (ClassNotFoundException e) {
            logger.info("class not found: {}", e.getMessage());

            System.exit(ExitCode.INTERNAL_ERROR);
        }
    }

    public int run(List<Task> tasks) {
        Evaluator evaluator = new Evaluator();

        try {
            Result result = evaluator.evaluate(tasks);

            //logger.info(result.getCsv());
        } catch (Throwable t) {
            logger.error("error", t);

            return ExitCode.FAIL;
        }

        return ExitCode.SUCCESS;
    }
}
