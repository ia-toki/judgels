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
}
