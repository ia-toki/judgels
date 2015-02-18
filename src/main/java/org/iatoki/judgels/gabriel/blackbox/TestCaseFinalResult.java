package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.collect.Lists;
import org.iatoki.judgels.gabriel.Verdict;

import java.util.List;
import java.util.Set;

public final class TestCaseFinalResult {
    private final Verdict verdict;
    private final String score;
    private final SandboxExecutionResult executionResult;
    private final List<Integer> subtaskIds;

    public TestCaseFinalResult(TestCaseResult result, SandboxExecutionResult executionResult, Set<Integer> subtaskIds) {
        this.verdict = new Verdict(result.getVerdict().getCode(), result.getVerdict().getDescription());
        this.score = result.getScore();
        this.executionResult = executionResult;
        this.subtaskIds = Lists.newArrayList(subtaskIds);
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public String getScore() {
        return score;
    }

    public SandboxExecutionResult getExecutionResult() {
        return executionResult;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }
}
