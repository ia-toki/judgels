package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.Verdict;

import java.util.Set;

public final class TestCaseFinalResult {
    private final Verdict verdict;
    private final String score;
    private final SandboxExecutionResultDetails details;
    private final Set<Integer> subtaskIds;

    public TestCaseFinalResult(TestCaseResult result, SandboxExecutionResultDetails details, Set<Integer> subtaskIds) {
        this.verdict = new Verdict(result.getVerdict().getCode(), result.getVerdict().getName());
        this.score = result.getScore();
        this.details = details;
        this.subtaskIds = subtaskIds;
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

    public Set<Integer> getSubtaskIds() {
        return subtaskIds;
    }
}
