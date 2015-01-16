package org.iatoki.judgels.gabriel.sandboxes;

import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.SandboxProvider;

import java.io.File;

public class FakeSandboxProvider implements SandboxProvider {

    private final File baseDir;
    private int sandboxId;

    public FakeSandboxProvider(File baseDir) {
        this.baseDir = baseDir;
        this.sandboxId = 0;
    }

    @Override
    public Sandbox newSandbox() {
        sandboxId++;
        return new FakeSandbox(new File(baseDir, "" + sandboxId));
    }
}
