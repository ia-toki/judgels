package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.Verdict;

public final class SubtaskResult {
    private final Verdict verdict;
    private final double score;

    public SubtaskResult(Verdict verdict, double score) {
        this.verdict = verdict;
        this.score = score;
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public double getScore() {
        return score;
    }
}
