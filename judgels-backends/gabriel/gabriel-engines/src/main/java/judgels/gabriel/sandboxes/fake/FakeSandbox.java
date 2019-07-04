package judgels.gabriel.sandboxes.fake;

import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import judgels.gabriel.api.ProcessExecutionResult;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxException;
import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.SandboxExecutionStatus;
import judgels.gabriel.sandboxes.SandboxExecutor;
import org.apache.commons.io.FileUtils;

public class FakeSandbox implements Sandbox {
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
            throw new SandboxException(e);
        }
    }

    @Override
    public File getFile(String filename) {
        return new File(baseDir, filename);
    }

    @Override
    public void addAllowedDirectory(File directory) {}

    @Override
    public void setTimeLimitInMilliseconds(int timeLimit) {}

    @Override
    public void setMemoryLimitInKilobytes(int memoryLimit) {}

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
            throw new SandboxException(e);
        }
    }

    @Override
    public void removeAllFilesExcept(Set<String> filenamesToRetain) {
        for (String filename : filenames) {
            if (!filenamesToRetain.contains(filename)) {
                try {
                    FileUtils.forceDelete(new File(baseDir, filename));
                } catch (IOException e) {
                    throw new SandboxException(e);
                }
            }
        }

        filenames.removeIf(f -> !filenamesToRetain.contains(f));
    }

    @Override
    public SandboxExecutionResult execute(List<String> command) {
        ProcessBuilder pb = getProcessBuilder(command).redirectErrorStream(true);

        try {
            ProcessExecutionResult result = SandboxExecutor.executeProcessBuilder(pb);
            return getResult(result.getExitCode());

        } catch (IOException | InterruptedException e) {
            return new SandboxExecutionResult.Builder()
                    .status(SandboxExecutionStatus.INTERNAL_ERROR)
                    .time(-1)
                    .memory(-1)
                    .message(e.getMessage())
                    .build();
        }
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
        return new SandboxExecutionResult.Builder()
                .time(100)
                .memory(1000)
                .status(status)
                .message("OK")
                .build();
    }
}
