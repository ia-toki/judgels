package org.iatoki.judgels.gabriel.blackbox;

public final class SubtaskResult {
    private final NormalVerdict verdict;
    private final double score;

    public SubtaskResult(NormalVerdict verdict, double score) {
        this.verdict = verdict;
        this.score = score;
    }

    public NormalVerdict getVerdict() {
        return verdict;
    }

    public double getScore() {
        return score;
    }
}
