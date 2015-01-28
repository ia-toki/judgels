package org.iatoki.judgels.gabriel.blackbox;

import com.google.gson.Gson;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.Verdict;

public final class BlackBoxGradingResult implements GradingResult {
    private final BlackBoxVerdict verdict;
    private final int score;
    private final BlackBoxGradingResultDetails details;

    private BlackBoxGradingResult(BlackBoxVerdict verdict, int score, BlackBoxGradingResultDetails details) {
        this.verdict = verdict;
        this.score = score;
        this.details = details;
    }

    public static BlackBoxGradingResult internalErrorResult() {
        return new BlackBoxGradingResult(GeneralVerdict.INTERNAL_ERROR, 0, BlackBoxGradingResultDetails.internalErrorDetails());
    }

    public static BlackBoxGradingResult compilationErrorResult(String compilationOutput) {
        return new BlackBoxGradingResult(CompilationVerdict.COMPILATION_ERROR, 0, BlackBoxGradingResultDetails.compilationErrorDetails(compilationOutput));
    }
    public static BlackBoxGradingResult normalResult(OverallResult result, BlackBoxGradingResultDetails details) {
        return new BlackBoxGradingResult(result.getVerdict(), result.getScore(), details);
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
    public String getDetails() {
        return new Gson().toJson(details);
    }
}
