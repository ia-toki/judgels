package org.iatoki.judgels.gabriel.sandboxes;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.SandboxExecutionStatus;
import org.iatoki.judgels.gabriel.SandboxFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FakeSandboxFactory implements SandboxFactory {

    private final File baseDir;
    private int sandboxesCount;
    private final List<List<SandboxExecutionStatus>> arrangedStatusesList;

    public FakeSandboxFactory(File baseDir, List<List<SandboxExecutionStatus>> arrangedStatusesList) {
        this.baseDir = baseDir;
        this.sandboxesCount = 0;
        this.arrangedStatusesList = arrangedStatusesList;
    }

    @Override
    public Sandbox newSandbox() {
        sandboxesCount++;
        File sandboxDir = new File(baseDir, "" + sandboxesCount);

        try {
            FileUtils.forceMkdir(sandboxDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (sandboxesCount >= arrangedStatusesList.size()) {
            return new FakeSandbox(sandboxDir, ImmutableList.of());
        } else {
            return new FakeSandbox(sandboxDir, arrangedStatusesList.get(sandboxesCount - 1));
        }
    }
}
