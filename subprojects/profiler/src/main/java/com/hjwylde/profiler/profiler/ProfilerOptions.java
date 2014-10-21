package com.hjwylde.profiler.profiler;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.0
 */
public final class ProfilerOptions implements Serializable {

    public static final ProfilerOptions DEFAULT_OPTIONS = builder().build();

    public static final int DEFAULT_WARMUP_RUNS = 20;
    public static final int DEFAULT_RUNS = 20;

    private static final long serialVersionUID = 1L;

    private final int warmupRuns;
    private final int runs;

    protected ProfilerOptions(Builder builder) {
        checkArgument(builder.warmupRuns >= 0, "builder.warmupRuns cannot be negative");
        checkArgument(builder.runs > 0, "builder.runs must be greater than 0");

        this.warmupRuns = builder.warmupRuns;
        this.runs = builder.runs;
    }

    public static Builder builder(ProfilerOptions options) {
        return new Builder(options);
    }

    /**
     * Gets a new builder.
     *
     * @return the builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    public int getRuns() {
        return runs;
    }

    public int getWarmupRuns() {
        return warmupRuns;
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.1.0
     */
    public static class Builder {

        private int warmupRuns;
        private int runs;

        protected Builder() {
            this.warmupRuns = DEFAULT_WARMUP_RUNS;
            this.runs = DEFAULT_RUNS;
        }

        protected Builder(ProfilerOptions options) {
            this.warmupRuns = options.warmupRuns;
            this.runs = options.runs;
        }

        public ProfilerOptions build() {
            return new ProfilerOptions(this);
        }

        /**
         * Sets the runs field.
         *
         * @param runs the runs field.
         * @return this for method chaining.
         */
        public final Builder setRuns(String runs) {
            return setRuns(Integer.valueOf(runs));
        }

        /**
         * Sets the runs field.
         *
         * @param runs the runs field.
         * @return this for method chaining.
         */
        public final Builder setRuns(int runs) {
            this.runs = runs;
            return this;
        }

        /**
         * Sets the warmup runs field.
         *
         * @param warmupRuns the warmup runs field.
         * @return this for method chaining.
         */
        public final Builder setWarmupRuns(String warmupRuns) {
            return setRuns(Integer.valueOf(warmupRuns));
        }

        /**
         * Sets the warmup runs field.
         *
         * @param warmupRuns the warmup runs field.
         * @return this for method chaining.
         */
        public final Builder setWarmupRuns(int warmupRuns) {
            this.warmupRuns = warmupRuns;
            return this;
        }
    }
}
