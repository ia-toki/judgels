package org.iatoki.judgels.gabriel.blackbox.sandboxes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.GabrielLogger;
import org.iatoki.judgels.gabriel.blackbox.SandboxUtils;
import org.iatoki.judgels.gabriel.SandboxException;
import org.iatoki.judgels.gabriel.blackbox.ProcessExecutionResult;
import org.iatoki.judgels.gabriel.blackbox.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionResult;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionStatus;

import java.io.File;
import java.io.IOException;
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
    private int blocksQuota;
    private int inodesQuota;
    private int maxProcesses;

    public MoeIsolateSandbox(String isolatePath, int boxId) {
        this.isolatePath = isolatePath;
        this.boxId = boxId;

        this.allowedDirs = Sets.newHashSet();

        this.filenames = Sets.newHashSet();


        GabrielLogger.getLogger().info("Creation of control groups of box {} started.", boxId);
        createControlGroups();
        GabrielLogger.getLogger().info("Creation of control groups of box {} finished", boxId);


        GabrielLogger.getLogger().info("Initialization of Isolate box {} started.", boxId);
        initIsolate();
        GabrielLogger.getLogger().info("Initialization of Isolate box {} finished.", boxId);

        this.allowedDirs.add(new File("/etc"));
        this.allowedDirs.add(this.boxDir);

        setQuota(100 * 1024, 20); // 100 MB, 20 files
        setMaxProcesses(6);
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
    public void setQuota(int blocks, int inodes) {
        this.blocksQuota = blocks;
        this.inodesQuota = inodes;
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
        GabrielLogger.getLogger().info("Cleanup of Isolate box {} started.", boxId);
        cleanUpIsolate();
        GabrielLogger.getLogger().info("Cleanup of Isolate box {} finished.", boxId);

        GabrielLogger.getLogger().info("Deletion of control groups of box {} started.", boxId);
        deleteControlGroups();
        GabrielLogger.getLogger().info("Deletion of control groups of box {} finished.", boxId);
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
            sandboxedCommand.add("-w" + timeLimit / 500.0);
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
        if (exitCode != 0 && exitCode != 1) {
            return SandboxExecutionResult.internalError("Isolate returns nonzero and non-one exit code: " + exitCode);
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
            return SandboxExecutionResult.internalError("Isolate did not produce readable meta file!");
        }

    }

    private void createControlGroups() {
        ProcessBuilder pb = new ProcessBuilder("cgcreate", "-g", "cpuset,cpuacct,memory:/box-" + boxId).redirectErrorStream(true);

        try {
            ProcessExecutionResult result = SandboxUtils.executeProcessBuilder(pb);
            if (result.getExitCode() != 0) {
                throw new SandboxException("Cannot create control groups!");
            }
        } catch (IOException | InterruptedException e) {
            throw new SandboxException(e);
        }
    }

    private void initIsolate() {
        ImmutableList.Builder<String> command = ImmutableList.builder();
        command.add(isolatePath, "-b" + boxId);

        if (blocksQuota > 0) {
            command.add("-q" + blocksQuota + "," + inodesQuota);
        }

        command.add("--init");

        ProcessBuilder pb = new ProcessBuilder(command.build()).redirectErrorStream(true);

        try {
            ProcessExecutionResult result = SandboxUtils.executeProcessBuilder(pb);
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
            ProcessExecutionResult result = SandboxUtils.executeProcessBuilder(pb);
            if (result.getExitCode() != 0) {
                throw new SandboxException("Cannot clean up Isolate!");
            }
        } catch (IOException | InterruptedException e) {
            throw new SandboxException(e);
        }
    }

    private void deleteControlGroups() {
        ProcessBuilder pb = new ProcessBuilder("cgdelete", "-g", "cpuset,cpuacct,memory:/box-" + boxId).redirectErrorStream(true);

        try {
            ProcessExecutionResult result = SandboxUtils.executeProcessBuilder(pb);
            if (result.getExitCode() != 0) {
                throw new SandboxException("Cannot delete control groups!");
            }
        } catch (IOException | InterruptedException e) {
            throw new SandboxException(e);
        }
    }
}
