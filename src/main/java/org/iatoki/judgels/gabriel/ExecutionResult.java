package org.iatoki.judgels.gabriel;

public final class ExecutionResult {
    private final int exitCode;
    private final int timeInMilliseconds;
    private final int memoryInKilobytes;
    private final Verdict verdict;
    private final String message;

    public ExecutionResult(int exitCode, int timeInMilliseconds, int memoryInKilobytes, Verdict verdict, String message) {
        this.exitCode = exitCode;
        this.timeInMilliseconds = timeInMilliseconds;
        this.memoryInKilobytes = memoryInKilobytes;
        this.verdict = verdict;
        this.message = message;
    }

    public int getExitCode() {
        return exitCode;
    }

    public int getTimeInMilliseconds() {
        return timeInMilliseconds;
    }

    public int getMemoryInKilobytes() {
        return memoryInKilobytes;
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public String getMessage() {
        return message;
    }
}
