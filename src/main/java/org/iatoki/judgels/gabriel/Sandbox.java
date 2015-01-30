package org.iatoki.judgels.gabriel;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface Sandbox {

    void addFile(File file);

    boolean containsFile(String filename);

    File getFile(String filename);

    void addAllowedDirectory(File directory);

    void setTimeLimitInMilliseconds(int timeLimit);

    void setMemoryLimitInKilobytes(int memoryLimit);

    void setStackSizeInKilobytes(int stackSizeInKilobytes);

    void setMaxProcesses(int maxProcesses);

    void setStandardInput(String filenameInsideThisSandbox);

    void setStandardOutput(String filenameInsideThisSandbox);

    void setStandardError(String filenameInsideThisSandbox);

    void setStandardInput(File file);

    void setStandardOutput(File file);

    void setStandardError(File file);

    void removeAllFilesExcept(Set<String> filenamesToRetain);

    void cleanUp();

    SandboxExecutionResult execute(List<String> command);
}
