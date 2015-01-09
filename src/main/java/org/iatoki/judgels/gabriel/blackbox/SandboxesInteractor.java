package org.iatoki.judgels.gabriel.blackbox;

import java.util.List;

public interface SandboxesInteractor {
    SandboxExecutionResult[] executeInteraction(Sandbox sandbox1, List<String> command1, Sandbox sandbox2, List<String> command2);
}
