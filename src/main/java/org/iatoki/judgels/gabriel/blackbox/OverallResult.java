package org.iatoki.judgels.gabriel.blackbox;

public final class OverallResult {
    private final NormalVerdict verdict;
    private final String message;
    private final int score;

    public OverallResult(NormalVerdict verdict, String message, int score) {
        this.verdict = verdict;
        this.message = message;
        this.score = score;
    }

    public NormalVerdict getVerdict() {
        return verdict;
    }

    public String getMessage() {
        return message;
    }

    public int getScore() {
        return score;
    }
}
