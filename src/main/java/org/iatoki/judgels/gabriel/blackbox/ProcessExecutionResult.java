package org.iatoki.judgels.gabriel.blackbox;

import java.util.List;

public final class ProcessExecutionResult {
    private final int exitCode;
    private final List<String> outputLines;

    public ProcessExecutionResult(int exitCode, List<String> outputLines) {
        this.exitCode = exitCode;
        this.outputLines = outputLines;
    }

    public int getExitCode() {
        return exitCode;
    }

    public List<String> getOutputLines() {
        return outputLines;
    }
}
