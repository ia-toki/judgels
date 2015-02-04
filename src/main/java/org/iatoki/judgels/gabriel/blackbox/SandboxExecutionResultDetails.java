package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.base.MoreObjects;

public final class SandboxExecutionResultDetails {
    private final int exitCode;
    private final int timeInMilliseconds;
    private final int memoryInKilobytes;
    private final String message;

    public SandboxExecutionResultDetails(int exitCode, int timeInMilliseconds, int memoryInKilobytes, String message) {
        this.exitCode = exitCode;
        this.timeInMilliseconds = timeInMilliseconds;
        this.memoryInKilobytes = memoryInKilobytes;
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

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("exitCode", exitCode)
                .add("timeInMilliseconds", timeInMilliseconds)
                .add("memoryInKilobytes", memoryInKilobytes)
                .add("message", message)
                .toString();
    }
}
