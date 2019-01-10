package org.iatoki.judgels.gabriel.blackbox;

import com.google.gson.Gson;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.Verdict;

import java.util.Map;

public final class BlackBoxGradingResults {

    private BlackBoxGradingResults() {
        // prevent instantiation
    }

    public static GradingResult compilationErrorResult(Map<String, String> compilationOutput) {
        BlackBoxVerdict blackBoxVerdict = CompilationVerdict.COMPILATION_ERROR;
        Verdict verdict = new Verdict(blackBoxVerdict.getCode(), blackBoxVerdict.getDescription());
        BlackBoxGradingResultDetails details = BlackBoxGradingResultDetails.compilationErrorDetails(compilationOutput);

        return new GradingResult(verdict, 0, new Gson().toJson(details));
    }

    public static GradingResult normalResult(ReductionResult result, BlackBoxGradingResultDetails details) {
        BlackBoxVerdict blackBoxVerdict = result.getVerdict();
        Verdict verdict = new Verdict(blackBoxVerdict.getCode(), blackBoxVerdict.getDescription());

        return new GradingResult(verdict, result.getScore(), new Gson().toJson(details));
    }
}
