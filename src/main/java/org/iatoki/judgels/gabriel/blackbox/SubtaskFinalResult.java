package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.Verdict;

public final class SubtaskFinalResult {
    private final Verdict verdict;
    private final double score;

    public SubtaskFinalResult(SubtaskResult result) {
        this.verdict = new Verdict(result.getVerdict().getCode(), result.getVerdict().getName());
        this.score = result.getScore();
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public double getScore() {
        return score;
    }
}
