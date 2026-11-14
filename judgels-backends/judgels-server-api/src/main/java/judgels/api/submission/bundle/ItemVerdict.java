package judgels.api.submission.bundle;

public enum ItemVerdict {
    PENDING_REGRADE,
    PENDING_MANUAL_GRADING,
    INTERNAL_ERROR,

    OK,
    ACCEPTED,
    WRONG_ANSWER
}
