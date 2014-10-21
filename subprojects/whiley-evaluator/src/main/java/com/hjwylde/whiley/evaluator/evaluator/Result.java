package com.hjwylde.whiley.evaluator.evaluator;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.profiler.profiler.Task;

import java.util.List;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.0
 */
public final class Result {

    private final List<Task> tasks;

    private final String csv;

    public Result(Builder builder) {
        this.tasks = builder.tasks;

        this.csv = checkNotNull(builder.csv);
    }

    public static Builder builder(List<Task> tasks) {
        return new Builder(tasks);
    }

    public String getCsv() {
        return csv;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public static final class Builder {

        private final List<Task> tasks;

        private String csv;

        private Builder(List<Task> tasks) {
            this.tasks = checkNotNull(tasks, "tasks cannot be null");
        }

        public Result build() {
            return new Result(this);
        }

        public Builder setCsv(String csv) {
            this.csv = csv;
            return this;
        }
    }
}
