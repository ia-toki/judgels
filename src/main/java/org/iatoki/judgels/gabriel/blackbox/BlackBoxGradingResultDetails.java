package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class BlackBoxGradingResultDetails {
    private final String compilationOutput;
    private final List<List<TestCaseConcreteResult>> testDataResults;
    private final List<SubtaskConcreteResult> subtaskResults;

    public BlackBoxGradingResultDetails(String compilationOutput, List<List<TestCaseConcreteResult>> testDataResults, List<SubtaskConcreteResult> subtaskResults) {
        this.compilationOutput = compilationOutput;
        this.testDataResults = testDataResults;
        this.subtaskResults = subtaskResults;
    }
    public String getCompilationOutput() {
        return compilationOutput;
    }

    public List<List<TestCaseConcreteResult>> getTestDataResults() {
        return testDataResults;
    }

    public List<SubtaskConcreteResult> getSubtaskResults() {
        return subtaskResults;
    }

    public static BlackBoxGradingResultDetails internalErrorDetails() {
        return new BlackBoxGradingResultDetails("(not compiled yet)", ImmutableList.of(), ImmutableList.of());
    }

    public static BlackBoxGradingResultDetails compilationErrorDetails(String compilationOutput) {
        return new BlackBoxGradingResultDetails(compilationOutput, ImmutableList.of(), ImmutableList.of());
    }
}
