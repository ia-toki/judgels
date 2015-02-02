package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class BlackBoxGradingResultDetails {
    private final String compilationOutput;
    private final List<TestCaseFinalResult> sampleTestDataResults;
    private final List<List<TestCaseFinalResult>> testDataResults;
    private final List<SubtaskFinalResult> subtaskResults;

    public BlackBoxGradingResultDetails(String compilationOutput, List<TestCaseFinalResult> sampleTestDataResults, List<List<TestCaseFinalResult>> testDataResults, List<SubtaskFinalResult> subtaskResults) {
        this.compilationOutput = compilationOutput;
        this.sampleTestDataResults = sampleTestDataResults;
        this.testDataResults = testDataResults;
        this.subtaskResults = subtaskResults;
    }
    public String getCompilationOutput() {
        return compilationOutput;
    }

    public List<TestCaseFinalResult> getSampleTestDataResults() {
        return sampleTestDataResults;
    }

    public List<List<TestCaseFinalResult>> getTestDataResults() {
        return testDataResults;
    }

    public List<SubtaskFinalResult> getSubtaskResults() {
        return subtaskResults;
    }

    public static BlackBoxGradingResultDetails internalErrorDetails() {
        return new BlackBoxGradingResultDetails("(not compiled yet)", ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
    }

    public static BlackBoxGradingResultDetails compilationErrorDetails(String compilationOutput) {
        return new BlackBoxGradingResultDetails(compilationOutput, ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
    }
}
