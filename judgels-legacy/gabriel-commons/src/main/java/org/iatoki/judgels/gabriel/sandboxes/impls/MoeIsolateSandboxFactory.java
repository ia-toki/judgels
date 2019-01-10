package org.iatoki.judgels.gabriel.sandboxes.impls;

import org.iatoki.judgels.gabriel.sandboxes.Sandbox;
import org.iatoki.judgels.gabriel.sandboxes.SandboxFactory;
import org.iatoki.judgels.gabriel.sandboxes.SandboxesInteractor;

public final class MoeIsolateSandboxFactory implements SandboxFactory {

    private final String isolatePath;
    private final String iwrapperPath;

    public MoeIsolateSandboxFactory(String isolatePath, String iwrapperPath) {
        this.isolatePath = isolatePath;
        this.iwrapperPath = iwrapperPath;
    }

    @Override
    public Sandbox newSandbox() {
        return new MoeIsolateSandbox(isolatePath, MoeIsolateBoxIdFactory.newBoxId());
    }

    @Override
    public SandboxesInteractor newSandboxesInteractor() {
        return new MoeIwrapperSandboxesInteractor(iwrapperPath);
    }
}
