package judgels.gabriel.api;

public class Verdicts {
    private Verdicts() {}

    public static final Verdict PENDING = Verdict.of("?", "Pending");
    public static final Verdict SKIPPED = Verdict.of("SKP", "Skipped");

    public static final Verdict COMPILATION_ERROR = Verdict.of("CE", "Compilation Error");

    public static final Verdict OK = Verdict.of("OK", "OK");
    public static final Verdict ACCEPTED = Verdict.of("AC", "Accepted");
    public static final Verdict WRONG_ANSWER = Verdict.of("WA", "Wrong Answer");

    public static final Verdict TIME_LIMIT_EXCEEDED = Verdict.of("TLE", "Time Limit Exceeded");
    public static final Verdict RUNTIME_ERROR = Verdict.of("RTE", "Runtime Error");

    public static final Verdict INTERNAL_ERROR = Verdict.of("!!!", "Internal Error");
}
