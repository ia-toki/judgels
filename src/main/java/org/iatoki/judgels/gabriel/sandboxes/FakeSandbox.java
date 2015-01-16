package org.iatoki.judgels.gabriel.sandboxes;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.ExecutionStatus;
import org.iatoki.judgels.gabriel.ExecutionVerdict;
import org.iatoki.judgels.gabriel.Sandbox;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class FakeSandbox implements Sandbox {

    private final File baseDir;
    private String standardInput;
    private String standardOutput;

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
    public void setStackSize(int stackSizeInKilobytes) {
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
        // nothing
    }

    @Override
    public ExecutionVerdict execute(List<String> command) {

        ImmutableList.Builder<String> commandBuilder = ImmutableList.builder();
        commandBuilder.addAll(command);

        if (standardInput != null) {
            commandBuilder.add("<", standardInput);
        }
        if (standardOutput != null) {
            commandBuilder.add(">", standardOutput);
        }

        List<String> newCommand = commandBuilder.build();

        try {
            Runtime.getRuntime().exec(newCommand.toArray(new String[newCommand.size()]), null, baseDir).waitFor();
        } catch (IOException | InterruptedException e) {

        }
        return new ExecutionVerdict(0, 1, 1024, ExecutionStatus.OK, "ok");
    }
}
