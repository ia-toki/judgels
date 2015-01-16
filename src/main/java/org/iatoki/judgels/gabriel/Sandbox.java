package org.iatoki.judgels.gabriel;

import java.io.File;
import java.util.List;

public interface Sandbox {

    void addFile(File file);

    File getFile(String filename);

    void addAllowedDirectory(File directory);

    void setTimeLimitInMilliseconds(int timeLimit);

    void setMemoryLimitInKilobytes(int memoryLimit);

    void setStackSize(int stackSizeInKilobytes);

    void setMaxProcesses(int maxProcesses);

    void setStandardInput(String standardInput);

    void setStandardOutput(String standardOutput);

    void setStandardError(String standardError);

    ExecutionVerdict execute(List<String> command);
}
