package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.Verdict;

public final class BlackBoxGradingResult implements GradingResult {
    private final Verdict verdict;
    private final int score;
    private final String message;
    private final BlackBoxGradingResultDetails details;

    private BlackBoxGradingResult(Verdict verdict, int score, String message, BlackBoxGradingResultDetails details) {
        this.verdict = verdict;
        this.score = score;
        this.message = message;
        this.details = details;
    }

    public static BlackBoxGradingResult internalError() {
        return new BlackBoxGradingResult(Verdict.INTERNAL_ERROR, 0, "Please report to judges", null);
    }

    public static BlackBoxGradingResult compileError(String compileErrorMessage) {
        BlackBoxGradingResultDetails details = new BlackBoxGradingResultDetails(compileErrorMessage, null, null, null);
        return new BlackBoxGradingResult(Verdict.COMPILE_ERROR, 0, "Compilation error", details);
    }

    public static BlackBoxGradingResult pending() {
        return new BlackBoxGradingResult(Verdict.PENDING, 0, "", null);
    }

    public static BlackBoxGradingResult ok(Verdict verdict, int score, BlackBoxGradingResultDetails details) {
        return new BlackBoxGradingResult(verdict, score, "Grading OK", details);
    }

    @Override
    public Verdict getVerdict() {
        return verdict;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public BlackBoxGradingResultDetails getDetails() {
        return details;
    }
}
