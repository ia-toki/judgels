package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.ExecutionVerdict;

public final class ScoringVerdict {
    private final ExecutionVerdict executionVerdict;
    private final ScoringStatus status;
    private final String value;

    public ScoringVerdict(ExecutionVerdict executionVerdict, ScoringStatus status, String value) {
        this.executionVerdict = executionVerdict;
        this.status = status;
        this.value = value;
    }

    public ExecutionVerdict getExecutionVerdict() {
        return executionVerdict;
    }

    public ScoringStatus getStatus() {
        return status;
    }

    public String getValue() {
        return value;
    }
}
