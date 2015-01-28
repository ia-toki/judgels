package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class BlackBoxGradingResultDetails {
    private final String compilationOutput;
    private final List<TestCaseDetailedResult> sampleTestDataResults;
    private final List<List<TestCaseDetailedResult>> testDataResults;
    private final List<SubtaskResult> subtaskResults;

    public BlackBoxGradingResultDetails(String compilationOutput, List<TestCaseDetailedResult> sampleTestDataResults, List<List<TestCaseDetailedResult>> testDataResults, List<SubtaskResult> subtaskResults) {
        this.compilationOutput = compilationOutput;
        this.sampleTestDataResults = sampleTestDataResults;
        this.testDataResults = testDataResults;
        this.subtaskResults = subtaskResults;
    }
    public String getCompilationOutput() {
        return compilationOutput;
    }

    public List<TestCaseDetailedResult> getSampleTestDataResults() {
        return sampleTestDataResults;
    }

    public List<List<TestCaseDetailedResult>> getTestDataResults() {
        return testDataResults;
    }

    public List<SubtaskResult> getSubtaskResults() {
        return subtaskResults;
    }

    public static BlackBoxGradingResultDetails internalErrorDetails() {
        return new BlackBoxGradingResultDetails("(not compiled yet)", ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
    }

    public static BlackBoxGradingResultDetails compilationErrorDetails(String compilationOutput) {
        return new BlackBoxGradingResultDetails(compilationOutput, ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
    }
}
