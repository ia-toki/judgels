package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

public final class BlackBoxGradingResultDetails {
    private final Map<String, String> compilationOutputs;
    private final List<TestGroupFinalResult> testDataResults;
    private final List<SubtaskFinalResult> subtaskResults;

    private BlackBoxGradingResultDetails(Map<String, String> compilationOutputs, List<TestGroupFinalResult> testDataResults, List<SubtaskFinalResult> subtaskResults) {
        this.compilationOutputs = compilationOutputs;
        this.testDataResults = testDataResults;
        this.subtaskResults = subtaskResults;
    }

    public Map<String, String> getCompilationOutputs() {
        return compilationOutputs;
    }

    public List<TestGroupFinalResult> getTestDataResults() {
        return testDataResults;
    }

    public List<SubtaskFinalResult> getSubtaskResults() {
        return subtaskResults;
    }

    public static BlackBoxGradingResultDetails emptyDetails() {
        return new BlackBoxGradingResultDetails(ImmutableMap.of(), ImmutableList.of(), ImmutableList.of());
    }

    public static BlackBoxGradingResultDetails compilationErrorDetails(Map<String, String> compilationOutput) {
        return new BlackBoxGradingResultDetails(compilationOutput, ImmutableList.of(), ImmutableList.of());
    }

    public static BlackBoxGradingResultDetails normalDetails(Map<String, String> compilationOutputs, List<TestGroupFinalResult> testDataResults, List<SubtaskFinalResult> subtaskResults) {
        return new BlackBoxGradingResultDetails(compilationOutputs, testDataResults, subtaskResults);
    }
}
