package org.iatoki.judgels.gabriel.blackbox;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TestCaseResult that = (TestCaseResult) o;

        if (!score.equals(that.score)) {
            return false;
        }
        if (!verdict.equals(that.verdict)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = verdict.hashCode();
        result = 31 * result + score.hashCode();
        return result;
    }
}
