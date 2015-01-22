package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.Verdict;

public final class ScoringResult {
    private final Verdict verdict;
    private final String score;

    public ScoringResult(Verdict verdict, String score) {
        this.verdict = verdict;
        this.score = score;
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public String getScore() {
        return score;
    }
}
