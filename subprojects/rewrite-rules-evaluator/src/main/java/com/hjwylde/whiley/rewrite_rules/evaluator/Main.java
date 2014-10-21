package com.hjwylde.whiley.rewrite_rules.evaluator;

import static com.google.common.collect.Sets.filter;

import com.hjwylde.whiley.rewrite_rules.evaluator.util.Result;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.ClassPath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public final class Main {

    private static final ImmutableList<Result> WYCS_TEST_RESULTS;

    static {
        try {
            ImmutableList.Builder<Result> builder = ImmutableList.builder();
            for (InputStream in : getResultInputStreams()) {

                try (InputStreamReader ir = new InputStreamReader(
                        in); BufferedReader br = new BufferedReader(ir)) {

                    builder.add(Result.from(br.lines()));
                }
            }

            WYCS_TEST_RESULTS = builder.build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Map<String, List<Double>> successResults = new HashMap<>();
        Map<String, List<Double>> failResults = new HashMap<>();

        for (Result result : WYCS_TEST_RESULTS) {
            Map<String, Double> ruleCounts = new HashMap<>(
                    result.getNormalisedInferenceRuleCounts());
            ruleCounts.putAll(result.getNormalisedReductionRuleCounts());

            for (String rule : ruleCounts.keySet()) {
                double count = ruleCounts.get(rule);

                if (result.isSuccess()) {
                    successResults.putIfAbsent(rule, new ArrayList<>());
                    successResults.get(rule).add(count);
                } else {
                    failResults.putIfAbsent(rule, new ArrayList<>());
                    failResults.get(rule).add(count);
                }
            }
        }

        Path path = Paths.get("out/");

        try {
            Files.createDirectories(path);

            List<String> lines = successResults.entrySet().stream().map(
                    e -> e.getKey() + "," + Joiner.on(",").join(e.getValue())).collect(
                    Collectors.toList());
            Files.write(path.resolve("success.csv"), lines, StandardCharsets.UTF_8,
                    StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE);

            lines = failResults.entrySet().stream().map(e -> e.getKey() + "," + Joiner.on(",").join(
                    e.getValue())).collect(Collectors.toList());
            Files.write(path.resolve("fail.csv"), lines, StandardCharsets.UTF_8,
                    StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ImmutableList<InputStream> getResultInputStreams() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<ClassPath.ResourceInfo> resources = ClassPath.from(classLoader).getResources();

        resources = filter(resources, input -> input.getResourceName().startsWith("wycs-tests"));

        ImmutableList.Builder<InputStream> builder = ImmutableList.builder();
        for (ClassPath.ResourceInfo resource : resources) {
            builder.add(classLoader.getResourceAsStream(resource.getResourceName()));
        }

        return builder.build();
    }
}
