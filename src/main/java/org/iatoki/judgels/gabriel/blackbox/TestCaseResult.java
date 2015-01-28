package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.SandboxExecutionStatus;

public final class TestCaseResult {
    private final NormalVerdict verdict;
    private final String score;

    private TestCaseResult(NormalVerdict verdict, String score) {
        this.verdict = verdict;
        this.score = score;
    }

    public static TestCaseResult fromScoringResult(ScoringResult scoringResult) {
        return new TestCaseResult(scoringResult.getVerdict(), scoringResult.getScore());
    }

    public static TestCaseResult fromEvaluationResult(EvaluationResult evaluationResult) {
        return new TestCaseResult(evaluationResult.getVerdict(), "-");
    }

    public NormalVerdict getVerdict() {
        return verdict;
    }

    public String getScore() {
        return score;
    }
}
