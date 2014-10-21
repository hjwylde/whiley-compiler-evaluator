package com.hjwylde.profiler;

import static com.google.common.base.Preconditions.checkArgument;

import com.hjwylde.profiler.profiler.Profiler;
import com.hjwylde.profiler.profiler.ProfilerOptions;
import com.hjwylde.profiler.profiler.Result;
import com.hjwylde.profiler.profiler.Task;
import com.hjwylde.profiler.util.ExitCode;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.0
 */
public final class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String OPT_WARMUP_RUNS = "warmup-runs";
    private static final String OPT_RUNS = "runs";
    private static final String OPT_HELP = "help";

    private static final Options OPTIONS = generateCommandLineOptions();

    private final int warmupRuns;
    private final int runs;

    public Main(int warmupRuns, int runs) {
        this.warmupRuns = warmupRuns;
        this.runs = runs;
    }

    /**
     * Generates a {@link com.hjwylde.profiler.profiler.Task} from the given command line object.
     *
     * @param cl the command line object.
     * @return the generated task.
     */
    public static Task generateTask(CommandLine cl) {
        checkArgument(cl.getArgs().length == 2, "invalid number of arguments");

        String clazz = cl.getArgs()[0];
        String method = cl.getArgs()[1];

        return new Task(clazz, method);
    }

    /**
     * Generates a {@link com.hjwylde.profiler.profiler.Task} from the given command line
     * arguments.
     *
     * @param args the arguments.
     * @return the generated compile specification.
     * @throws org.apache.commons.cli.ParseException if the arguments cannot be parsed.
     */
    public static Task generateTask(String[] args) throws ParseException {
        return generateTask(new PosixParser().parse(OPTIONS, args));
    }

    /**
     * Main.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        try {
            CommandLine cl = new PosixParser().parse(OPTIONS, args);

            if (cl.hasOption(OPT_HELP)) {
                printHelp();
                System.exit(ExitCode.SUCCESS);
            } else if (cl.getArgs().length != 2) {
                throw new ParseException("invalid number of arguments passed");
            }

            int warmupRuns = ProfilerOptions.DEFAULT_WARMUP_RUNS;
            if (cl.hasOption(OPT_WARMUP_RUNS)) {
                warmupRuns = Integer.parseInt(cl.getOptionValue(OPT_WARMUP_RUNS));
            }
            int runs = ProfilerOptions.DEFAULT_RUNS;
            if (cl.hasOption(OPT_RUNS)) {
                runs = Integer.parseInt(cl.getOptionValue(OPT_RUNS));
            }

            Task task = generateTask(cl);

            System.exit(new Main(warmupRuns, runs).run(task));
        } catch (ParseException e) {
            String message = e.getMessage();
            if (message != null && !message.isEmpty()) {
                message = message.substring(0, 1).toLowerCase(Locale.ENGLISH) + message.substring(
                        1);
            } else {
                message = message == null ? null : message.toLowerCase(Locale.ENGLISH);
            }

            logger.error(message + "\n");
            printHelp();

            System.exit(ExitCode.FAIL);
        } catch (NumberFormatException e) {
            logger.error("illegal number: {}", e.getMessage());

            System.exit(ExitCode.FAIL);
        }
    }

    public int run(Task task) {
        ProfilerOptions.Builder builder = ProfilerOptions.builder();
        builder.setWarmupRuns(warmupRuns);
        builder.setRuns(runs);

        Profiler profiler = new Profiler(builder.build());

        try {
            Result result = profiler.profile(task);

            logger.info("{}, {}, {}, {}, {}, {}, {}", result.getTask().getClazz(),
                    result.getTask().getMethod(), result.getElapsed(), result.getRuns().size(),
                    result.getMean(), result.getVariance(), result.getStandardDeviation());
        } catch (Throwable t) {
            logger.error("error", t);

            return ExitCode.FAIL;
        }

        return ExitCode.SUCCESS;
    }

    /**
     * Gets the command line options for parsing the input.
     *
     * @return the command line options.
     */
    private static Options generateCommandLineOptions() {
        Options options = new Options();

        // Profiler options
        Option warmupRuns = OptionBuilder.withLongOpt(OPT_WARMUP_RUNS).hasArg().withArgName("long")
                .withDescription(
                        "Sets the number of warmup runs for the profiler process, may set to '0' to disable, defaults to '"
                                + ProfilerOptions.DEFAULT_WARMUP_RUNS + "'").create("w");
        Option runs = OptionBuilder.withLongOpt(OPT_RUNS).hasArg().withArgName("long")
                .withDescription("Sets the number of runs for the profiler process, defaults to '"
                        + ProfilerOptions.DEFAULT_RUNS + "'").create("r");

        Option help = new Option("h", OPT_HELP, false, "Prints this message");

        options.addOption(warmupRuns);
        options.addOption(runs);

        options.addOption(help);

        return options;
    }

    /**
     * Prints the help for how to call {@code profiler}.
     */
    private static void printHelp() {
        new HelpFormatter().printHelp(120, "profiler [options] class method", null, OPTIONS, null);
    }
}
