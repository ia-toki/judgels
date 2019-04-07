package judgels.gabriel.api;

import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface EvaluationResult {
    Verdict getVerdict();
    Optional<SandboxExecutionResult> getExecutionResult();

    static EvaluationResult plainResult(String result) {
        return new Builder()
                .verdict(Verdict.OK)
                .executionResult(SandboxExecutionResult.plainMessage(result))
                .build();
    }

    static EvaluationResult executedResult(SandboxExecutionResult result) {
        Verdict verdict;
        switch (result.getStatus()) {
            case ZERO_EXIT_CODE:
                verdict = Verdict.OK;
                break;
            case NONZERO_EXIT_CODE:
            case KILLED_ON_SIGNAL:
                verdict = Verdict.RUNTIME_ERROR;
                break;
            case TIMED_OUT:
                verdict = Verdict.TIME_LIMIT_EXCEEDED;
                break;
            default:
                throw new IllegalStateException();
        }
        return new Builder()
                .verdict(verdict)
                .executionResult(result)
                .build();
    }

    static EvaluationResult skippedResult() {
        return new Builder().verdict(Verdict.SKIPPED).build();
    }

    class Builder extends ImmutableEvaluationResult.Builder {}
}
