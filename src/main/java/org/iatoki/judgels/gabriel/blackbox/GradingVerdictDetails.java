package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.ExecutionVerdict;

import java.util.List;

public final class GradingVerdictDetails {
    private final String gradingMessage;
    private final ExecutionVerdict compilationExecutionVerdict;
    private final List<TestSetVerdict> testSetVerdicts;
    private final List<Double> subtaskScores;

    public GradingVerdictDetails(String gradingMessage, ExecutionVerdict compilationExecutionVerdict, List<TestSetVerdict> testSetVerdicts, List<Double> subtaskScores) {
        this.gradingMessage = gradingMessage;
        this.compilationExecutionVerdict = compilationExecutionVerdict;
        this.testSetVerdicts = testSetVerdicts;
        this.subtaskScores = subtaskScores;
    }

    public String getGradingMessage() {
        return gradingMessage;
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
