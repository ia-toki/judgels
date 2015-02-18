package org.iatoki.judgels.gabriel.blackbox.sandboxes;

import org.iatoki.judgels.gabriel.blackbox.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.SandboxFactory;
import org.iatoki.judgels.gabriel.blackbox.SandboxesInteractor;

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
