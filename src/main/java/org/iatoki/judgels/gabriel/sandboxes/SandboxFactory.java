package org.iatoki.judgels.gabriel.sandboxes;

public interface SandboxFactory {
    Sandbox newSandbox();

    SandboxesInteractor newSandboxesInteractor();
}
