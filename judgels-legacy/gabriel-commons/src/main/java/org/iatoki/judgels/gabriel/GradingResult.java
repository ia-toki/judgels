package org.iatoki.judgels.gabriel;

public final class GradingResult {

    private final Verdict verdict;
    private final int score;
    private final String details;

    public GradingResult(Verdict verdict, int score, String details) {
        this.verdict = verdict;
        this.score = score;
        this.details = details;
    }

    public static GradingResult internalErrorResult(String errorMessage) {
        return new GradingResult(new Verdict("!!!", "Internal Error"), 0, errorMessage);
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public int getScore() {
        return score;
    }

    public String getDetails() {
        return details;
    }
}
