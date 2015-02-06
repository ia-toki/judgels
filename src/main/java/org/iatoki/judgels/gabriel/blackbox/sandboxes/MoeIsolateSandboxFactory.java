package org.iatoki.judgels.gabriel.blackbox.sandboxes;

import org.iatoki.judgels.gabriel.blackbox.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.SandboxFactory;

public final class MoeIsolateSandboxFactory implements SandboxFactory {

    private final String isolatePath;
    private int boxId;

    public MoeIsolateSandboxFactory(String isolatePath) {
        this.isolatePath = isolatePath;
        this.boxId = 0;
    }

    @Override
    public Sandbox newSandbox() {
        boxId = (boxId + 1) % 100;
        return new MoeIsolateSandbox(isolatePath, boxId + 1);
    }
}
