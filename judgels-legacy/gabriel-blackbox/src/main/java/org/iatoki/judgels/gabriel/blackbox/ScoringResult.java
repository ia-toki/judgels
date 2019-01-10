package org.iatoki.judgels.gabriel.blackbox;

public final class ScoringResult {
    private final ScoringVerdict verdict;
    private final String score;

    public ScoringResult(ScoringVerdict verdict, String score) {
        this.verdict = verdict;
        this.score = score;
    }

    public ScoringVerdict getVerdict() {
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

        ScoringResult that = (ScoringResult) o;

        if (!score.equals(that.score)) {
            return false;
        }
        if (verdict != that.verdict) {
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
