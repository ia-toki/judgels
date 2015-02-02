package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.SandboxExecutionResultDetails;
import org.iatoki.judgels.gabriel.Verdict;

public final class TestCaseFinalResult {
    private final Verdict verdict;
    private final String score;
    private final SandboxExecutionResultDetails details;

    public TestCaseFinalResult(TestCaseResult testCaseResult, SandboxExecutionResultDetails details) {
        this.verdict = new Verdict(testCaseResult.getVerdict().getCode(), testCaseResult.getVerdict().getName());
        this.score = testCaseResult.getScore();
        this.details = details;
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public String getScore() {
        return score;
    }

    public SandboxExecutionResultDetails getDetails() {
        return details;
    }
}
