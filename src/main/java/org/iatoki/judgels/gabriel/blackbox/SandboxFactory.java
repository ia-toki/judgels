package org.iatoki.judgels.gabriel.blackbox;

public interface SandboxFactory {
    Sandbox newSandbox();

    SandboxesInteractor newSandboxesInteractor();
}
