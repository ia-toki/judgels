package org.iatoki.judgels.gabriel.blackbox.sandboxes;

import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.blackbox.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionResult;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public final class FakeSandbox extends Sandbox {
    private static final int FAKE_TIMED_OUT_EXIT_CODE = 10;
    private static final int FAKE_KILLED_ON_SIGNAL_EXIT_CODE = 20;

    private final File baseDir;
    private File standardInput;
    private File standardOutput;
    private File standardError;

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
    public void resetRedirections() {
        standardInput = null;
        standardOutput = null;
        standardError = null;
    }

    @Override
    public void redirectStandardInput(String filenameInsideThisSandbox) {
        standardInput = new File(baseDir, filenameInsideThisSandbox);
    }

    @Override
    public void redirectStandardOutput(String filenameInsideThisSandbox) {
        standardOutput = new File(baseDir, filenameInsideThisSandbox);
    }

    @Override
    public void redirectStandardError(String filenameInsideThisSandbox) {
        standardError = new File(baseDir, filenameInsideThisSandbox);
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

    @Override
    public ProcessBuilder getProcessBuilder(List<String> command) {
        String[] commandArray = command.toArray(new String[command.size()]);

        ProcessBuilder pb = new ProcessBuilder(commandArray);
        pb.directory(baseDir);

        if (standardInput != null) {
            pb.redirectInput(standardInput);
        }

        if (standardOutput != null) {
            pb.redirectOutput(standardOutput);
        }

        if (standardError != null) {
            pb.redirectError(standardError);
        }

        return pb;
    }

    @Override
    public SandboxExecutionResult getResult(int exitCode) {
        SandboxExecutionStatus status;
        switch (exitCode) {
            case 0:
                status = SandboxExecutionStatus.ZERO_EXIT_CODE;
                break;
            case FAKE_TIMED_OUT_EXIT_CODE:
                status = SandboxExecutionStatus.TIMED_OUT;
                break;
            case FAKE_KILLED_ON_SIGNAL_EXIT_CODE:
                status = SandboxExecutionStatus.KILLED_ON_SIGNAL;
                break;
            default:
                status = SandboxExecutionStatus.NONZERO_EXIT_CODE;
        }
        return new SandboxExecutionResult(status, 100, 1000, "OK");
    }
}
