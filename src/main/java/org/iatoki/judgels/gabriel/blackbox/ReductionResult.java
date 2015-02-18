package org.iatoki.judgels.gabriel.blackbox;

public final class ReductionResult {
    private final NormalVerdict verdict;
    private final int score;

    public ReductionResult(NormalVerdict verdict, int score) {
        this.verdict = verdict;
        this.score = score;
    }

    public NormalVerdict getVerdict() {
        return verdict;
    }

    public int getScore() {
        return score;
    }
}
