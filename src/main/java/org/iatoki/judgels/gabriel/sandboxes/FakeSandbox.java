package org.iatoki.judgels.gabriel.sandboxes;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.ExecutionResult;
import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.Verdict;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class FakeSandbox implements Sandbox {

    private final File baseDir;
    private String standardInput;
    private String standardOutput;
    private String standardError;

    public FakeSandbox(File baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public void addFile(File file) {
        try {
            FileUtils.copyFileToDirectory(file, baseDir);
        } catch (IOException e) {

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
    public ExecutionResult execute(List<String> command) {

        ImmutableList.Builder<String> commandBuilder = ImmutableList.builder();
        commandBuilder.addAll(command);

        if (standardInput != null) {
            commandBuilder.add("<", standardInput);
        }
        if (standardOutput != null) {
            commandBuilder.add(">", standardOutput);
        }
        if (standardError != null) {
            if (standardError == standardOutput) {
                commandBuilder.add("2>&1");
            } else {
                commandBuilder.add("2>", standardError);
            }
        }

        List<String> newCommand = commandBuilder.build();

        try {
            Runtime.getRuntime().exec(newCommand.toArray(new String[newCommand.size()]), null, baseDir).waitFor();
        } catch (IOException | InterruptedException e) {

        }
        return new ExecutionResult(0, 1, 1024, Verdict.OK, "ok");
    }

    @Override
    public boolean containsFile(String filename) {
        return true;
    }

    @Override
    public void cleanUp() {

    }
}
