package judgels.sandalphon.api.submission.bundle;

public enum Verdict {
    PENDING_REGRADE,
    PENDING_MANUAL_GRADING,
    INTERNAL_ERROR,

    OK,
    ACCEPTED,
    WRONG_ANSWER
}
