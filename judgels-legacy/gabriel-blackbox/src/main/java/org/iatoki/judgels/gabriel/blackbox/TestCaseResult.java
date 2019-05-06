package org.iatoki.judgels.gabriel.blackbox;

public final class TestCaseResult {
    private final NormalVerdict verdict;
    private final String score;

    public TestCaseResult(NormalVerdict verdict, String score) {
        this.verdict = verdict;
        this.score = score;
    }

    public NormalVerdict getVerdict() {
        return verdict;
    }

    public String getScore() {
        return score;
    }
}
