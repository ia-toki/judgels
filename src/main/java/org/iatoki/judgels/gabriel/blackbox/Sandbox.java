package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GabrielUtils;

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

    public abstract void resetRedirections();

    public abstract void redirectStandardInput(String filenameInsideThisSandbox);

    public abstract void redirectStandardOutput(String filenameInsideThisSandbox);

    public abstract void redirectStandardError(String filenameInsideThisSandbox);

    public abstract void removeAllFilesExcept(Set<String> filenamesToRetain);

    public abstract void cleanUp();

    public final SandboxExecutionResult execute(List<String> command) {
        ProcessBuilder pb = getProcessBuilder(command).redirectErrorStream(true);

        try {
            ProcessExecutionResult result = GabrielUtils.executeProcessBuilder(pb);
            return getResult(result.getExitCode());

        } catch (IOException | InterruptedException e) {
            return SandboxExecutionResult.internalError(e.getMessage());
        }
    }

    public abstract ProcessBuilder getProcessBuilder(List<String> command);

    public abstract SandboxExecutionResult getResult(int exitCode);
}
