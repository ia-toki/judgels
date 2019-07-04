package judgels.gabriel.sandboxes.moe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.gabriel.api.ProcessExecutionResult;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxException;
import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.SandboxExecutionStatus;
import judgels.gabriel.sandboxes.SandboxExecutor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsolateSandbox implements Sandbox {
    private static final Logger LOGGER = LoggerFactory.getLogger(IsolateSandbox.class);

    private final String isolatePath;
    private final int boxId;
    private final Set<File> allowedDirs;
    private final Set<String> filenames;

    private File boxDir;

    private File standardInput;
    private File standardOutput;
    private File standardError;

    private int timeLimit;
    private int memoryLimit;
    private int stackSize;
    private int fileSizeLimit;
    private int maxProcesses;

    public IsolateSandbox(String isolatePath, int boxId) {
        this.isolatePath = isolatePath;
        this.boxId = boxId;
        this.allowedDirs = Sets.newHashSet();
        this.filenames = Sets.newHashSet();
        this.fileSizeLimit = 100 * 1024;
        this.maxProcesses = 20;

        LOGGER.info("Initialization of Isolate box {} started.", boxId);
        initIsolate();
        LOGGER.info("Initialization of Isolate box {} finished.", boxId);
    }

    @Override
    public void addFile(File file) {
        try {
            FileUtils.copyFileToDirectory(file, boxDir);
            filenames.add(file.getName());
        } catch (IOException e) {
            throw new SandboxException(e);
        }
    }

    @Override
    public boolean containsFile(String filename) {
        return filenames.contains(filename);
    }

    @Override
    public File getFile(String filename) {
        return new File(boxDir, filename);
    }

    @Override
    public void addAllowedDirectory(File directory) {
        allowedDirs.add(directory);
    }

    @Override
    public void setTimeLimitInMilliseconds(int timeLimitInMilliseconds) {
        this.timeLimit = timeLimitInMilliseconds;
    }

    @Override
    public void setMemoryLimitInKilobytes(int memoryLimitInKilobytes) {
        this.memoryLimit = memoryLimitInKilobytes;
    }

    @Override
    public void resetRedirections() {
        standardInput = null;
        standardOutput = null;
        standardError = null;
    }

    @Override
    public void redirectStandardInput(String filenameInsideThisSandbox) {
        standardInput = new File(boxDir, filenameInsideThisSandbox);
    }

    @Override
    public void redirectStandardOutput(String filenameInsideThisSandbox) {
        standardOutput = new File(boxDir, filenameInsideThisSandbox);
    }

    @Override
    public void redirectStandardError(String filenameInsideThisSandbox) {
        standardError = new File(boxDir, filenameInsideThisSandbox);
    }

    @Override
    public void removeAllFilesExcept(Set<String> filenamesToRetain) {
        for (String filename : filenames) {
            if (!filenamesToRetain.contains(filename)) {
                try {
                    FileUtils.forceDelete(new File(boxDir, filename));
                } catch (IOException e) {
                    throw new SandboxException(e);
                }
            }
        }

        filenames.removeIf(f -> !filenamesToRetain.contains(f));
    }

    @Override
    public void cleanUp() {
        LOGGER.info("Cleanup of Isolate box {} started.", boxId);
        cleanUpIsolate();
        LOGGER.info("Cleanup of Isolate box {} finished.", boxId);
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
        ImmutableList.Builder<String> sandboxedCommand = ImmutableList.builder();

        sandboxedCommand.add(isolatePath).add("-b" + boxId);

        sandboxedCommand.add("--dir=/etc");
        for (File dir : allowedDirs) {
            sandboxedCommand.add("--dir=" + dir.getAbsolutePath() + ":rw");
        }

        sandboxedCommand.add("-e");

        if (maxProcesses > 0) {
            sandboxedCommand.add("--cg").add("--cg-timing").add("-p" + maxProcesses);
        }

        if (timeLimit > 0) {
            double timeLimitInSeconds = timeLimit / 1000.0;
            sandboxedCommand.add("-t" + timeLimitInSeconds);
            sandboxedCommand.add("-w" + (timeLimitInSeconds + Math.max(8.0, timeLimitInSeconds)));
        }

        if (memoryLimit > 0) {
            if (maxProcesses > 1) {
                sandboxedCommand.add("--cg-mem=" + memoryLimit);
            } else {
                sandboxedCommand.add("-m" + memoryLimit);
            }
        }

        if (fileSizeLimit > 0) {
            sandboxedCommand.add("-f" + fileSizeLimit);
        }

        if (stackSize > 0) {
            sandboxedCommand.add("-k" + stackSize);
        }

        if (standardInput != null) {
            sandboxedCommand.add("-i" + standardInput.getName());
        }

        if (standardOutput != null) {
            sandboxedCommand.add("-o" + standardOutput.getName());
        }

        if (standardError != null) {
            sandboxedCommand.add("-r" + standardError.getName());
        }

        sandboxedCommand.add("-M" + new File(boxDir, "_isolate.meta").getAbsolutePath());
        sandboxedCommand.add("--run").add("--");

        sandboxedCommand.addAll(command);

        return new ProcessBuilder(sandboxedCommand.build());
    }

    @Override
    public SandboxExecutionResult getResult(int exitCode) {
        if (exitCode != 0 && exitCode != 1) {
            return SandboxExecutionResult.internalError("Isolate returns nonzero and non-one exit code: " + exitCode);
        }

        String meta;
        try {
            meta = FileUtils.readFileToString(new File(boxDir, "_isolate.meta"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return SandboxExecutionResult.internalError("Isolate did not produce readable meta file!");
        }

        Map<String, String> items = Maps.newHashMap();
        for (String line : meta.split("\n")) {
            String[] tokens = line.split(":");
            String key = tokens[0];
            String val = tokens[1];

            items.put(key, val);
        }

        int time = (int) (Double.parseDouble(items.get("time")) * 1000);
        int wallTime = (int) (Double.parseDouble(items.get("time-wall")) * 1000);
        int memory = (int) (Double.parseDouble(items.get("cg-mem")));
        String status = items.get("status");

        SandboxExecutionStatus executionStatus;
        if (status == null) {
            executionStatus = SandboxExecutionStatus.ZERO_EXIT_CODE;
        } else if (status.equals("RE")) {
            executionStatus = SandboxExecutionStatus.NONZERO_EXIT_CODE;
        } else if (status.equals("SG")) {
            executionStatus = SandboxExecutionStatus.KILLED_ON_SIGNAL;
        } else if (status.equals("TO")) {
            executionStatus = SandboxExecutionStatus.TIMED_OUT;
        } else {
            executionStatus = SandboxExecutionStatus.INTERNAL_ERROR;
        }

        boolean isKilled = items.getOrDefault("killed", "0").equals("1");
        Optional<String> message = Optional.ofNullable(items.get("message"));

        return new SandboxExecutionResult.Builder()
                .time(time)
                .wallTime(wallTime)
                .memory(memory)
                .status(executionStatus)
                .isKilled(isKilled)
                .message(message)
                .build();
    }

    private void initIsolate() {
        ImmutableList.Builder<String> command = ImmutableList.builder();
        command.add(isolatePath, "-b" + boxId);
        command.add("--cg");
        command.add("--init");

        ProcessBuilder pb = new ProcessBuilder(command.build()).redirectErrorStream(true);

        try {
            ProcessExecutionResult result = SandboxExecutor.executeProcessBuilder(pb);
            if (result.getExitCode() != 0) {
                throw new SandboxException("Cannot initialize Isolate!");
            }

            boxDir = new File(result.getOutputLines().get(0), "box");
        } catch (IOException | InterruptedException e) {
            throw new SandboxException(e);
        }
    }

    private void cleanUpIsolate() {
        ProcessBuilder pb = new ProcessBuilder(isolatePath, "-b" + boxId, "--cleanup").redirectErrorStream(true);

        try {
            ProcessExecutionResult result = SandboxExecutor.executeProcessBuilder(pb);
            if (result.getExitCode() != 0) {
                throw new SandboxException("Cannot clean up Isolate!");
            }
            if (boxDir.exists()) {
                FileUtils.forceDelete(boxDir);
            }
        } catch (IOException | InterruptedException e) {
            throw new SandboxException(e);
        }
    }
}
