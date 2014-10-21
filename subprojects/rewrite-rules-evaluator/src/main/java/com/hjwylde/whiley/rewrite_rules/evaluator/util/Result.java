package com.hjwylde.whiley.rewrite_rules.evaluator.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public final class Result {

    private final String name;

    private final boolean success;

    private final ImmutableMap<String, Integer> reductionRuleCounts;
    private final ImmutableMap<String, Integer> inferenceRuleCounts;

    public Result(String name, boolean success, Map<String, Integer> reductionRuleCounts,
            Map<String, Integer> inferenceRuleCounts) {
        this.name = checkNotNull(name, "name cannot be null");

        this.success = success;

        this.reductionRuleCounts = ImmutableMap.copyOf(reductionRuleCounts);
        this.inferenceRuleCounts = ImmutableMap.copyOf(inferenceRuleCounts);
    }

    public static Result from(Stream<String> lines) {
        return from(lines.iterator());
    }

    public ImmutableMap<String, Integer> getInferenceRuleCounts() {
        return inferenceRuleCounts;
    }

    public String getName() {
        return name;
    }

    public ImmutableMap<String, Double> getNormalisedInferenceRuleCounts() {
        double total = getTotalNumberRuleCounts();

        ImmutableMap.Builder<String, Double> builder = ImmutableMap.builder();
        inferenceRuleCounts.forEach((k, v) -> builder.put(k, ((double) v) / total));

        return builder.build();
    }

    public ImmutableMap<String, Double> getNormalisedReductionRuleCounts() {
        double total = getTotalNumberRuleCounts();

        ImmutableMap.Builder<String, Double> builder = ImmutableMap.builder();
        reductionRuleCounts.forEach((k, v) -> builder.put(k, ((double) v) / total));

        return builder.build();
    }

    public ImmutableMap<String, Integer> getReductionRuleCounts() {
        return reductionRuleCounts;
    }

    public int getTotalNumberRuleCounts() {
        return reductionRuleCounts.values().parallelStream().reduce(0, Math::addExact)
                + inferenceRuleCounts.values().parallelStream().reduce(0, Math::addExact);
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "Result{" +
                "name='" + name + '\'' +
                ", success=" + success +
                ", reductionRuleCounts=" + getNormalisedReductionRuleCounts() +
                ", inferenceRuleCounts=" + getNormalisedInferenceRuleCounts() +
                '}';
    }

    private static Result from(Iterator<String> it) {
        String name = it.next();

        boolean success = Boolean.valueOf(it.next());

        Map<String, Integer> reductionRuleCounts = new HashMap<>();
        Map<String, Integer> inferenceRuleCounts = new HashMap<>();

        // Skip the title
        it.next();
        while (it.hasNext()) {
            String next = it.next();
            if (next.equals("Inference Rules")) {
                break;
            }

            String rule = next.substring(0, next.indexOf(","));
            int count = Integer.valueOf(next.substring(next.indexOf(",") + 1));

            reductionRuleCounts.put(rule, count);
        }

        while (it.hasNext()) {
            String next = it.next();

            String rule = next.substring(0, next.indexOf(","));
            int count = Integer.valueOf(next.substring(next.indexOf(",") + 1));

            inferenceRuleCounts.put(rule, count);
        }

        return new Result(name, success, reductionRuleCounts, inferenceRuleCounts);
    }
}
