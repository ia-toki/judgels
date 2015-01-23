package org.iatoki.judgels.gabriel.sandboxes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.ExecutionResult;
import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.Verdict;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public final class FakeSandbox implements Sandbox {

    private final File baseDir;
    private String standardInput;
    private String standardOutput;
    private String standardError;

    private final Set<String> files;

    public FakeSandbox(File baseDir) {
        this.baseDir = baseDir;
        this.files = Sets.newHashSet();
    }

    @Override
    public void addFile(File file) {
        try {
            FileUtils.copyFileToDirectory(file, baseDir);
            files.add(file.getName());
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

        try {
            pb.start().waitFor();
        } catch (IOException | InterruptedException e) {

        }

        return new ExecutionResult(0, 1, 1024, Verdict.OK, "ok");
    }

    @Override
    public boolean containsFile(String filename) {
        return files.contains(filename);
    }

    @Override
    public void cleanUp() {

    }
}
