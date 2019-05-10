package judgels.gabriel.api;

import java.util.List;

public interface SandboxInteractor {
    SandboxExecutionResult[] interact(Sandbox sandbox1, List<String> command1, Sandbox sandbox2, List<String> command2);
}
