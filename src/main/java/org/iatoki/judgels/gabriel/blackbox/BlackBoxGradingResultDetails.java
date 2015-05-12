package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

public final class BlackBoxGradingResultDetails {
    private final String errorMessage;
    private final Map<String, String> compilationOutputs;
    private final List<TestGroupFinalResult> testDataResults;
    private final List<SubtaskFinalResult> subtaskResults;

    public BlackBoxGradingResultDetails(String errorMessage) {
        this.errorMessage = errorMessage;
        this.compilationOutputs = ImmutableMap.of();
        this.testDataResults = ImmutableList.of();
        this.subtaskResults = ImmutableList.of();
    }

    public BlackBoxGradingResultDetails(Map<String, String> compilationOutputs, List<TestGroupFinalResult> testDataResults, List<SubtaskFinalResult> subtaskResults) {
        this.errorMessage = null;
        this.compilationOutputs = compilationOutputs;
        this.testDataResults = testDataResults;
        this.subtaskResults = subtaskResults;
    }

    public String getErrorMessage() {
        return errorMessage;
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

    public static BlackBoxGradingResultDetails internalErrorDetails(String errorMessage) {
        return new BlackBoxGradingResultDetails(errorMessage);
    }

    public static BlackBoxGradingResultDetails compilationErrorDetails(Map<String, String> compilationOutput) {
        return new BlackBoxGradingResultDetails(compilationOutput, ImmutableList.of(), ImmutableList.of());
    }
}
