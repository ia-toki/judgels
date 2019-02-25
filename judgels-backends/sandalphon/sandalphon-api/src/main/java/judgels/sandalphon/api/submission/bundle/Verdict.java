package judgels.sandalphon.api.submission.bundle;

public enum Verdict {
    PENDING,
    GRADING_NOT_NEEDED,
    PENDING_MANUAL_GRADING,
    INTERNAL_ERROR,

    OK,
    ACCEPTED,
    WRONG_ANSWER
}
