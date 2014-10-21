package com.hjwylde.profiler.profiler;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.math.LongMath.sqrt;
import static com.hjwylde.profiler.util.Functions.reduce;

import com.hjwylde.profiler.util.Function2;

import com.google.common.collect.ImmutableList;

import java.math.RoundingMode;
import java.util.List;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.0
 */
public final class Result {

    private final Task task;

    private final ImmutableList<Long> runs;
    private final boolean isSample;

    public Result(Builder builder) {
        this.task = builder.task;

        this.runs = ImmutableList.copyOf(builder.runs);
        this.isSample = builder.isSample;
    }

    public static Builder builder(Task task) {
        return new Builder(task);
    }

    public long getElapsed() {
        return reduce(new Function2<Long, Long, Long>() {
            @Override
            public Long apply(Long a, Long b) {
                return a + b;
            }
        }, 0L, runs);
    }

    public long getMean() {
        return getElapsed() / runs.size();
    }

    public ImmutableList<Long> getRuns() {
        return runs;
    }

    public long getStandardDeviation() {
        return sqrt(getVariance(), RoundingMode.HALF_EVEN);
    }

    public Task getTask() {
        return task;
    }

    public long getVariance() {
        long mean = getMean();

        long variance = 0;
        for (long run : runs) {
            long difference = run - mean;

            variance += difference * difference;
        }

        return variance / (runs.size() - (isSample ? 1 : 0));
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.1.0
     */
    public static final class Builder {

        private final Task task;

        private List<Long> runs;
        private boolean isSample;

        private Builder(Task task) {
            this.task = checkNotNull(task, "task cannot be null");
        }

        public Result build() {
            return new Result(this);
        }

        public Builder isSample(boolean isSample) {
            this.isSample = isSample;
            return this;
        }
        public Builder setRuns(List<Long> runs) {
            this.runs = runs;
            return this;
        }
    }
}
