package org.iatoki.judgels.gabriel.blackbox;

public final class SandboxExecutionResultDetails {
    private final int time;
    private final int memory;
    private final String message;

    public SandboxExecutionResultDetails(int timeInMilliseconds, int memoryInKilobytes, String message) {
        this.time = timeInMilliseconds;
        this.memory = memoryInKilobytes;
        this.message = message;
    }

    public static SandboxExecutionResultDetails internalError(String message) {
        return new SandboxExecutionResultDetails(-1, -1, message);
    }

    public int getTimeInMilliseconds() {
        return time;
    }

    public int getMemoryInKilobytes() {
        return memory;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
