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
            case OK:
                verdict = EvaluationVerdict.OK;
                break;
            case RUNTIME_ERROR:
                verdict = EvaluationVerdict.RUNTIME_ERROR;
                break;
            case TIME_LIMIT_EXCEEDED:
                verdict = EvaluationVerdict.TIME_LIMIT_EXCEEDED;
                break;
            case MEMORY_LIMIT_EXCEEDED:
                verdict = EvaluationVerdict.MEMORY_LIMIT_EXCEEDED;
                break;
            default:
                throw new IllegalArgumentException();
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
