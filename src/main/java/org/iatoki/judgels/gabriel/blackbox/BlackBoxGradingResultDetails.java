package org.iatoki.judgels.gabriel.blackbox;

import java.util.List;

public final class BlackBoxGradingResultDetails {
    private final String compilationOutput;
    private final List<List<ScoringResult>> sampleTestDataResults;
    private final List<List<ScoringResult>> testDataResults;
    private final List<SubtaskResult> subtaskResults;

    public BlackBoxGradingResultDetails(String compilationOutput, List<List<ScoringResult>> sampleTestDataResults, List<List<ScoringResult>> testDataResults, List<SubtaskResult> subtaskResults) {
        this.compilationOutput = compilationOutput;
        this.sampleTestDataResults = sampleTestDataResults;
        this.testDataResults = testDataResults;
        this.subtaskResults = subtaskResults;
    }

    public String getCompilationOutput() {
        return compilationOutput;
    }

    public List<List<ScoringResult>> getSampleTestDataResults() {
        return sampleTestDataResults;
    }

    public List<List<ScoringResult>> getTestDataResults() {
        return testDataResults;
    }

    public List<SubtaskResult> getSubtaskResults() {
        return subtaskResults;
    }
}
