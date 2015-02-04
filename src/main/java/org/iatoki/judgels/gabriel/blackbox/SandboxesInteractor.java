package org.iatoki.judgels.gabriel.blackbox;

import java.util.List;

public interface SandboxesInteractor {
    SandboxExecutionResult[] runInteraction(Sandbox sandbox1, List<String> command1, Sandbox sandbox2, List<String> command2);
}
