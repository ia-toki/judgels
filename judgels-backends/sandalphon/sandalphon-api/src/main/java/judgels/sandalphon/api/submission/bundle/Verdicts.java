package judgels.sandalphon.api.submission.bundle;

public class Verdicts {
    private Verdicts() {}

    public static final Verdict PENDING = Verdict.of("?", "Pending");
    public static final Verdict SKIPPED = Verdict.of("SKP", "Skipped");

    public static final Verdict OK = Verdict.of("OK", "OK");
    public static final Verdict ACCEPTED = Verdict.of("AC", "Accepted");
    public static final Verdict WRONG_ANSWER = Verdict.of("WA", "Wrong Answer");

    public static final Verdict GRADING_NOT_NEEDED = Verdict.of("NN", "Grading Not Needed");

    public static final Verdict PENDING_MANUAL_GRADING = Verdict.of("MG", "Pending Manual Grading");

    public static final Verdict INTERNAL_ERROR = Verdict.of("!!!", "Internal Error");
}
