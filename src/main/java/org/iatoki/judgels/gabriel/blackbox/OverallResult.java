package org.iatoki.judgels.gabriel.blackbox;

public final class OverallResult {
    private final NormalVerdict verdict;
    private final int score;

    public OverallResult(NormalVerdict verdict, int score) {
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
