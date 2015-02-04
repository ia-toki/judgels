package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

public final class BlackBoxGradingResultDetails {
    private final Map<String, String> compilationOutput;
    private final List<TestGroupFinalResult> testDataResults;
    private final List<SubtaskFinalResult> subtaskResults;

    public BlackBoxGradingResultDetails(Map<String, String> compilationOutput, List<TestGroupFinalResult> testDataResults, List<SubtaskFinalResult> subtaskResults) {
        this.compilationOutput = compilationOutput;
        this.testDataResults = testDataResults;
        this.subtaskResults = subtaskResults;
    }
    public Map<String, String> getCompilationOutput() {
        return compilationOutput;
    }

    public List<TestGroupFinalResult> getTestDataResults() {
        return testDataResults;
    }

    public List<SubtaskFinalResult> getSubtaskResults() {
        return subtaskResults;
    }

    public static BlackBoxGradingResultDetails internalErrorDetails() {
        return new BlackBoxGradingResultDetails(ImmutableMap.of(), ImmutableList.of(), ImmutableList.of());
    }

    public static BlackBoxGradingResultDetails compilationErrorDetails(Map<String, String> compilationOutput) {
        return new BlackBoxGradingResultDetails(compilationOutput, ImmutableList.of(), ImmutableList.of());
    }
}
