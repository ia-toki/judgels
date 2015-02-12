package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.Lists;
import org.iatoki.judgels.gabriel.Verdict;

import java.util.List;
import java.util.Set;

public final class TestCaseFinalResult {
    private final Verdict verdict;
    private final String score;
    private final SandboxExecutionResultDetails details;
    private final List<Integer> subtaskIds;

    public TestCaseFinalResult(TestCaseResult result, SandboxExecutionResultDetails details, Set<Integer> subtaskIds) {
        this.verdict = new Verdict(result.getVerdict().getCode(), result.getVerdict().getName());
        this.score = result.getScore();
        this.details = details;
        this.subtaskIds = Lists.newArrayList(subtaskIds);
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

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }
}
