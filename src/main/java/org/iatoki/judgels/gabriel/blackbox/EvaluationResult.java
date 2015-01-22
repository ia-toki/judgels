package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.ExecutionResult;

import java.io.File;
import java.util.Map;

public final class EvaluationResult {
    private final ExecutionResult executionResult;
    private final Map<String, File> evaluationOutputFiles;

    public EvaluationResult(ExecutionResult executionResult, Map<String, File> evaluationOutputFiles) {
        this.executionResult = executionResult;
        this.evaluationOutputFiles = evaluationOutputFiles;
    }

    public ExecutionResult getExecutionResult() {
        return executionResult;
    }

    public Map<String, File> getEvaluationOutputFiles() {
        return evaluationOutputFiles;
    }
}
