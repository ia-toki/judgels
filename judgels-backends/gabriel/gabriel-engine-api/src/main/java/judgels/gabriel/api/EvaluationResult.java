package judgels.gabriel.api;

import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface EvaluationResult {
    EvaluationVerdict getVerdict();
    Optional<SandboxExecutionResult> getExecutionResult();

    static EvaluationResult plainResult(String result) {
        return new Builder()
                .verdict(EvaluationVerdict.OK)
                .executionResult(SandboxExecutionResult.plainMessage(result))
                .build();
    }

    static EvaluationResult executedResult(SandboxExecutionResult result) {
        EvaluationVerdict verdict;
        switch (result.getStatus()) {
            case ZERO_EXIT_CODE:
                verdict = EvaluationVerdict.OK;
                break;
            case NONZERO_EXIT_CODE:
            case KILLED_ON_SIGNAL:
                verdict = EvaluationVerdict.RUNTIME_ERROR;
                break;
            case TIMED_OUT:
                verdict = EvaluationVerdict.TIME_LIMIT_EXCEEDED;
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
        return new Builder().verdict(EvaluationVerdict.SKIPPED).build();
    }

    class Builder extends ImmutableEvaluationResult.Builder {}
}
