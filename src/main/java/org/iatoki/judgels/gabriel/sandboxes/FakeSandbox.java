package org.iatoki.judgels.gabriel.sandboxes;

import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.SandboxExecutionResult;
import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.SandboxExecutionResultDetails;
import org.iatoki.judgels.gabriel.SandboxExecutionStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public final class FakeSandbox implements Sandbox {
    private static final int TLE_EXIT_CODE = 10;
    private static final int MLE_EXIT_CODE = 20;
    private static final int RTE_EXIT_CODE = 30;

    private final File baseDir;
    private String standardInput;
    private String standardOutput;
    private String standardError;

    private final Set<String> filenames;

    public FakeSandbox(File baseDir) {
        this.baseDir = baseDir;
        this.filenames = Sets.newHashSet();
    }

    @Override
    public void addFile(File file) {
        try {
            FileUtils.copyFileToDirectory(file, baseDir);
            filenames.add(file.getName());
        } catch (IOException e) {
            // should never happen
        }
    }

    @Override
    public File getFile(String filename) {
        return new File(baseDir, filename);
    }

    @Override
    public void addAllowedDirectory(File directory) {
        // nothing
    }

    @Override
    public void setTimeLimitInMilliseconds(int timeLimit) {
        // nothing
    }

    @Override
    public void setMemoryLimitInKilobytes(int memoryLimit) {
        // nothing
    }

    @Override
    public void setStackSizeInKilobytes(int stackSizeInKilobytes) {
        // nothing
    }

    @Override
    public void setMaxProcesses(int maxProcesses) {
        // nothing
    }

    @Override
    public void setStandardInput(String standardInput) {
        this.standardInput = standardInput;
    }

    @Override
    public void setStandardOutput(String standardOutput) {
        this.standardOutput = standardOutput;
    }

    @Override
    public void setStandardError(String standardError) {
        this.standardError = standardError;
    }

    @Override
    public SandboxExecutionResult execute(List<String> command) {
        String[] commandArray = command.toArray(new String[command.size()]);

        ProcessBuilder pb = new ProcessBuilder(commandArray);
        pb.directory(baseDir);

        if (standardInput != null) {
            pb.redirectInput(new File(baseDir, standardInput));
        }
        if (standardOutput != null) {
            pb.redirectOutput(new File(baseDir, standardOutput));
        }
        if (standardError != null) {
            pb.redirectError(new File(baseDir, standardError));
        }

        int exitCode;
        try {
            exitCode = pb.start().waitFor();
        } catch (IOException | InterruptedException e) {
            return new SandboxExecutionResult(SandboxExecutionStatus.INTERNAL_ERROR, new SandboxExecutionResultDetails(1, 100, 1000, "Execution error"));
        }

        SandboxExecutionStatus status;
        switch (exitCode) {
            case 0:
                status = SandboxExecutionStatus.OK;
                break;
            case TLE_EXIT_CODE:
                status = SandboxExecutionStatus.TIME_LIMIT_EXCEEDED;
                break;
            case MLE_EXIT_CODE:
                status = SandboxExecutionStatus.MEMORY_LIMIT_EXCEEDED;
                break;
            default:
                status = SandboxExecutionStatus.RUNTIME_ERROR;
        }
        return new SandboxExecutionResult(status, new SandboxExecutionResultDetails(0, 100, 1000, "OK"));
    }

    @Override
    public boolean containsFile(String filename) {
        return filenames.contains(filename);
    }

    @Override
    public void cleanUp() {
        try {
            FileUtils.deleteDirectory(baseDir);
        } catch (IOException e) {
            // should never happen
        }
    }

    @Override
    public void removeAllFilesExcept(Set<String> filenamesToRetain) {
        for (String filename : filenames) {
            if (!filenamesToRetain.contains(filename)) {
                try {
                    FileUtils.forceDelete(new File(baseDir, filename));
                } catch (IOException e) {
                    // should never happen
                }
            }
        }

        filenames.removeIf(f -> !filenamesToRetain.contains(f));
    }
}
