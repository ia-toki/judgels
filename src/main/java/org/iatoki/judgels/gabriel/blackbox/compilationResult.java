package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.ExecutionResult;

import java.io.File;
import java.util.Map;

public final class CompilationResult {
    private final String output;
    private final ExecutionResult executionResult;
    private final Map<String, File> executableFiles;

    public CompilationResult(String output, ExecutionResult executionResult, Map<String, File> executableFiles) {
        this.output = output;
        this.executionResult = executionResult;
        this.executableFiles = executableFiles;
    }

    public String getOutput() {
        return output;
    }

    public ExecutionResult getExecutionResult() {
        return executionResult;
    }

    public Map<String, File> getExecutableFiles() {
        return executableFiles;
    }
}
