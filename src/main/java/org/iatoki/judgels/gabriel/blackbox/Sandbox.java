package org.iatoki.judgels.gabriel.blackbox;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public abstract class Sandbox {

    public abstract void addFile(File file);

    public abstract boolean containsFile(String filename);

    public abstract File getFile(String filename);

    public abstract void addAllowedDirectory(File directory);

    public abstract void setTimeLimitInMilliseconds(int timeLimit);

    public abstract void setMemoryLimitInKilobytes(int memoryLimit);

    public abstract void setStackSizeInKilobytes(int stackSizeInKilobytes);

    public abstract void setMaxProcesses(int maxProcesses);

    public abstract void setStandardInput(String filenameInsideThisSandbox);

    public abstract void setStandardOutput(String filenameInsideThisSandbox);

    public abstract void setStandardError(String filenameInsideThisSandbox);

    public abstract void removeAllFilesExcept(Set<String> filenamesToRetain);

    public abstract void cleanUp();

    public final SandboxExecutionResult execute(List<String> command) {
        ProcessBuilder pb = getProcessBuilder(command);
        int exitCode;
        try {
            exitCode = pb.start().waitFor();
        } catch (IOException | InterruptedException e) {
            return new SandboxExecutionResult(SandboxExecutionStatus.INTERNAL_ERROR, new SandboxExecutionResultDetails(1, 100, 1000, "Execution error"));
        }

        return getResult(exitCode);
    }

    protected abstract ProcessBuilder getProcessBuilder(List<String> command);

    protected abstract SandboxExecutionResult getResult(int exitCode);
}
