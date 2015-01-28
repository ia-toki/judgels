package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.SandboxExecutionResult;
import org.iatoki.judgels.gabriel.SandboxExecutionResultDetails;

public final class TestCaseDetailedResult {
    private final NormalVerdict verdict;
    private final String score;
    private final SandboxExecutionResultDetails details;

    public TestCaseDetailedResult(TestCaseResult testCaseResult, SandboxExecutionResultDetails details) {
        this.verdict = testCaseResult.getVerdict();
        this.score = testCaseResult.getScore();
        this.details = details;
    }

    public NormalVerdict getVerdict() {
        return verdict;
    }

    public String getScore() {
        return score;
    }

    public SandboxExecutionResultDetails getDetails() {
        return details;
    }
}
