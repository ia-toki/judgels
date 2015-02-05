package org.iatoki.judgels.gabriel.blackbox;

public final class EvaluationResult {
    private final EvaluationVerdict verdict;
    private final SandboxExecutionResultDetails details;

    private EvaluationResult(EvaluationVerdict verdict, SandboxExecutionResultDetails details) {
        this.verdict = verdict;
        this.details = details;
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
        return new EvaluationResult(verdict, result.getDetails());
    }

    public static EvaluationResult skippedResult() {
        return new EvaluationResult(EvaluationVerdict.SKIPPED, null);
    }

    public EvaluationVerdict getVerdict() {
        return verdict;
    }

    public SandboxExecutionResultDetails getDetails() {
        return details;
    }
}
