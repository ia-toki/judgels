package org.iatoki.judgels.gabriel.blackbox;

public final class EvaluationResult {
    private final EvaluationVerdict verdict;
    private final SandboxExecutionResult executionResult;

    private EvaluationResult(EvaluationVerdict verdict, SandboxExecutionResult executionResult) {
        this.verdict = verdict;
        this.executionResult = executionResult;
    }

    public static EvaluationResult executedResult(SandboxExecutionResult result) {
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
        return new EvaluationResult(verdict, result);
    }

    public static EvaluationResult skippedResult() {
        return new EvaluationResult(EvaluationVerdict.SKIPPED, null);
    }

    public EvaluationVerdict getVerdict() {
        return verdict;
    }

    public SandboxExecutionResult getExecutionResult() {
        return executionResult;
    }
}
