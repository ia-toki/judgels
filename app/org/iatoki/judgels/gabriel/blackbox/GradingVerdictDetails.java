package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.ExecutionVerdict;

import java.util.List;

public final class GradingVerdictDetails {
    private final ExecutionVerdict compilationExecutionVerdict;

    private final List<TestSetVerdict> testSetVerdicts;
    private final List<Double> subtaskScores;

    public GradingVerdictDetails(ExecutionVerdict compilationExecutionVerdict, List<TestSetVerdict> testSetVerdicts, List<Double> subtaskScores) {
        this.compilationExecutionVerdict = compilationExecutionVerdict;
        this.testSetVerdicts = testSetVerdicts;
        this.subtaskScores = subtaskScores;
    }

    public ExecutionVerdict getCompilationExecutionVerdict() {
        return compilationExecutionVerdict;
    }

    public List<TestSetVerdict> getTestSetVerdicts() {
        return testSetVerdicts;
    }

    public List<Double> getSubtaskScores() {
        return subtaskScores;
    }
}
