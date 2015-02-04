package org.iatoki.judgels.gabriel.blackbox;

public final class SandboxExecutionResult {
    private final SandboxExecutionStatus status;
    private final SandboxExecutionResultDetails details;

    public SandboxExecutionResult(SandboxExecutionStatus status, SandboxExecutionResultDetails details) {
        this.status = status;
        this.details = details;
    }

    public SandboxExecutionStatus getStatus() {
        return status;
    }

    public SandboxExecutionResultDetails getDetails() {
        return details;
    }
}
