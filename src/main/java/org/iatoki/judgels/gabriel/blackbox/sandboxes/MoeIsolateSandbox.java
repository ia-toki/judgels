package org.iatoki.judgels.gabriel.blackbox.sandboxes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.SandboxException;
import org.iatoki.judgels.gabriel.blackbox.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionResult;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MoeIsolateSandbox extends Sandbox {
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
    private int maxProcesses;

    public MoeIsolateSandbox(String isolatePath, int boxId) {
        this.isolatePath = isolatePath;
        this.boxId = boxId;

        this.allowedDirs = Sets.newHashSet();
        this.allowedDirs.add(new File("/etc"));

        this.filenames = Sets.newHashSet();

        createControlGroups();
        initIsolate();
    }

    @Override
    public void addFile(File file) {
        try {
            FileUtils.copyFileToDirectory(file, boxDir);
            filenames.add(file.getName());
        } catch (IOException e) {
            // should never happen
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
    public void setTimeLimitInMilliseconds(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    @Override
    public void setMemoryLimitInKilobytes(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    @Override
    public void setStackSizeInKilobytes(int stackSizeInKilobytes) {
        this.stackSize = stackSizeInKilobytes;
    }

    @Override
    public void setMaxProcesses(int maxProcesses) {
        this.maxProcesses = maxProcesses;
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
        cleanUpIsolate();
    }

    @Override
    public ProcessBuilder getProcessBuilder(List<String> command) {
        ImmutableList.Builder<String> sandboxedCommand = ImmutableList.builder();

        sandboxedCommand.add(isolatePath).add("-b" + boxId);

        for (File dir : allowedDirs) {
            sandboxedCommand.add("--dir=" + dir.getAbsolutePath() + ":rw");
        }

        sandboxedCommand.add("--full-env");

        if (maxProcesses > 0) {
            sandboxedCommand.add("--cg").add("--cg-timing").add("-p" + maxProcesses);
        }

        if (timeLimit > 0) {
            sandboxedCommand.add("-t" + timeLimit / 1000.0);
        }

        if (memoryLimit > 0) {
            if (maxProcesses > 1) {
                sandboxedCommand.add("--cg-mem=" + memoryLimit);
            } else {
                sandboxedCommand.add("-m" + memoryLimit);
            }
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
        if (exitCode != 0) {
            return SandboxExecutionResult.internalError("Isolate returns nonzero exit code: " + exitCode);
        }

        try {
            String meta = FileUtils.readFileToString(new File(boxDir, "_isolate.meta"));

            Map<String, String> items = Maps.newHashMap();
            for (String line : meta.split("\n")) {
                String[] tokens = line.split(":");
                String key = tokens[0];
                String val = tokens[1];

                items.put(key, val);
            }

            int time = (int) (Double.parseDouble(items.get("time")) * 1000);
            int memory = (int) (Double.parseDouble(items.get("cg-mem")));
            String status = items.get("status");

            if (status == null) {
                return new SandboxExecutionResult(SandboxExecutionStatus.ZERO_EXIT_CODE, time, memory, meta);
            } else if (status.equals("RE")) {
                return new SandboxExecutionResult(SandboxExecutionStatus.NONZERO_EXIT_CODE, time, memory, meta);
            } else if (status.equals("SG")) {
                return new SandboxExecutionResult(SandboxExecutionStatus.KILLED_ON_SIGNAL, time, memory, meta);
            } else if (status.equals("TO")) {
                return new SandboxExecutionResult(SandboxExecutionStatus.TIMED_OUT, time, memory, meta);
            } else {
                return new SandboxExecutionResult(SandboxExecutionStatus.INTERNAL_ERROR, time, memory, meta);
            }
        } catch (IOException e) {
            return SandboxExecutionResult.internalError("Isolate did not produce readable meta file");
        }

    }

    private void createControlGroups() {
        try {
            int exitCode = new ProcessBuilder("cgcreate", "-g", "cpuset,cpuacct,memory:/box-" + boxId).start().waitFor();
            if (exitCode != 0) {
                throw new SandboxException("Cannot create control groups");
            }
        } catch (IOException | InterruptedException e) {
            throw new SandboxException(e);
        }
    }

    private void initIsolate() {
        try {
            Process p = new ProcessBuilder(isolatePath, "-b" + boxId, "--init").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            boxDir = new File(reader.readLine(), "box");
            int exitCode = p.waitFor();
            if (exitCode != 0) {
                throw new SandboxException("Cannot init isolate");
            }

            if (!boxDir.setWritable(true) || boxDir.setReadable(true) || boxDir.setExecutable(true)) {
                throw new SandboxException("Cannot set box directory to rwx");
            }
        } catch (IOException | InterruptedException e) {
            throw new SandboxException(e);
        }
    }

    private void cleanUpIsolate() {
        try {
            int exitCode = new ProcessBuilder(isolatePath, "-b" + boxId, "--cleanup").start().waitFor();
            if (exitCode != 0) {
                throw new SandboxException("Cannot clean up isolate");
            }
        } catch (IOException | InterruptedException e) {
            throw new SandboxException(e);
        }
    }
}
