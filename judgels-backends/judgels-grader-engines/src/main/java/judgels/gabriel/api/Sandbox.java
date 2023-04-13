package judgels.gabriel.api;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface Sandbox {
    void addFile(File file);
    boolean containsFile(String filename);
    File getFile(String filename);
    void addAllowedDirectory(File directory);
    void setTimeLimitInMilliseconds(int timeLimit);
    void setWallTimeLimitInMilliseconds(int timeLimit);
    void setMemoryLimitInKilobytes(int memoryLimit);
    void resetRedirections();
    void redirectStandardInput(String filenameInsideThisSandbox);
    void redirectStandardOutput(String filenameInsideThisSandbox);
    void redirectStandardError(String filenameInsideThisSandbox);
    void removeAllFilesExcept(Set<String> filenamesToRetain);
    void cleanUp();

    SandboxExecutionResult execute(List<String> command);
    ProcessBuilder getProcessBuilder(List<String> command);
    SandboxExecutionResult getResult(int exitCode);
}
