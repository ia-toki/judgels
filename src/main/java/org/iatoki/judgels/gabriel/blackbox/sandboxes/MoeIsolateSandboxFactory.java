package org.iatoki.judgels.gabriel.blackbox.sandboxes;

import org.iatoki.judgels.gabriel.blackbox.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.SandboxFactory;

public final class MoeIsolateSandboxFactory implements SandboxFactory {

    private final String isolatePath;

    public MoeIsolateSandboxFactory(String isolatePath) {
        this.isolatePath = isolatePath;
    }

    @Override
    public Sandbox newSandbox() {
        return new MoeIsolateSandbox(isolatePath, MoeIsolateBoxIdFactory.newBoxId());
    }
}
