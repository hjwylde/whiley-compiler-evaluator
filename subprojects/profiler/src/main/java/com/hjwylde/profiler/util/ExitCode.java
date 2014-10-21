package com.hjwylde.profiler.util;

/**
 * Holds the exit code constants.
 *
 * @author Henry J. Wylde
 * @since 0.1.0
 */
public final class ExitCode {

    /**
     * Program ran successfully.
     */
    public static final int SUCCESS = 0;
    /**
     * Program failed to run due to a user input error.
     */
    public static final int FAIL = 1;
    /**
     * Program failed to run due to an internal error.
     */
    public static final int INTERNAL_ERROR = 2;

    /**
     * This class cannot be instantiated.
     */
    private ExitCode() {}
}

