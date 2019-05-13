package org.iatoki.judgels.gabriel.blackbox;

import com.google.gson.Gson;
import judgels.gabriel.api.GradingResult;
import judgels.gabriel.api.Verdict;

import java.util.Map;

public final class BlackBoxGradingResults {

    private BlackBoxGradingResults() {
        // prevent instantiation
    }

    public static GradingResult compilationErrorResult(Map<String, String> compilationOutput) {
        Verdict verdict = Verdict.COMPILATION_ERROR;
        BlackBoxGradingResultDetails details = BlackBoxGradingResultDetails.compilationErrorDetails(compilationOutput);

        return new GradingResult.Builder()
                .verdict(verdict)
                .score(0)
                .details(new Gson().toJson(details))
                .build();
    }

    public static GradingResult normalResult(SubtaskResult result, BlackBoxGradingResultDetails details) {
        BlackBoxVerdict blackBoxVerdict = result.getVerdict();
        Verdict verdict = Verdict.OK;

        switch (blackBoxVerdict.getCode()) {
            case "AC": verdict = Verdict.ACCEPTED; break;
            case "WA": verdict = Verdict.WRONG_ANSWER; break;
            case "TLE": verdict = Verdict.TIME_LIMIT_EXCEEDED; break;
            case "RTE": verdict = Verdict.RUNTIME_ERROR; break;
            case "OK": verdict = Verdict.OK; break;
        }

        return new GradingResult.Builder()
                .verdict(verdict)
                .score((int) result.getScore())
                .details(new Gson().toJson(details))
                .build();
    }
}
