package org.iatoki.judgels.gabriel.blackbox;

import com.google.gson.Gson;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.Verdict;

import java.util.Map;

public final class BlackBoxGradingResult implements GradingResult {

    private final Verdict verdict;
    private final int score;
    private final BlackBoxGradingResultDetails details;

    private BlackBoxGradingResult(BlackBoxVerdict verdict, int score, BlackBoxGradingResultDetails details) {
        this.verdict = new Verdict(verdict.getCode(), verdict.getDescription());
        this.score = score;
        this.details = details;
    }

    public static BlackBoxGradingResult internalErrorResult(String errorMessage) {
        return new BlackBoxGradingResult(GeneralVerdict.INTERNAL_ERROR, 0, BlackBoxGradingResultDetails.internalErrorDetails(errorMessage));
    }

    public static BlackBoxGradingResult compilationErrorResult(Map<String, String> compilationOutput) {
        return new BlackBoxGradingResult(CompilationVerdict.COMPILATION_ERROR, 0, BlackBoxGradingResultDetails.compilationErrorDetails(compilationOutput));
    }

    public static BlackBoxGradingResult normalResult(ReductionResult result, BlackBoxGradingResultDetails details) {
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
    public String getDetailsAsJson() {
        return new Gson().toJson(details);
    }

    public BlackBoxGradingResultDetails getDetails() {
        return details;
    }
}
