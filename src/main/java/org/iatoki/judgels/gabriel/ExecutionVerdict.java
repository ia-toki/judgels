package org.iatoki.judgels.gabriel;

public final class ExecutionVerdict {
    private final int exitCode;
    private final int timeInMilliseconds;
    private final int memoryInKilobytes;
    private final ExecutionStatus status;
    private final String message;

    public ExecutionVerdict(int exitCode, int timeInMilliseconds, int memoryInKilobytes, ExecutionStatus status, String message) {
        this.exitCode = exitCode;
        this.timeInMilliseconds = timeInMilliseconds;
        this.memoryInKilobytes = memoryInKilobytes;
        this.status = status;
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

    public ExecutionStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
