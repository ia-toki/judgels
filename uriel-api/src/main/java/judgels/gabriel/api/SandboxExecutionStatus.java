package judgels.gabriel.api;

public enum SandboxExecutionStatus {
    ZERO_EXIT_CODE,
    NONZERO_EXIT_CODE,
    KILLED_ON_SIGNAL,
    TIMED_OUT,
    INTERNAL_ERROR
}
