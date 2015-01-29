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

    public FakeSandboxFactory(File baseDir) {
        this.baseDir = baseDir;
        this.sandboxesCount = 0;
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

        return new FakeSandbox(sandboxDir);
    }
}
