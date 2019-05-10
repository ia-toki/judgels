package judgels.gabriel.sandboxes.moe;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.List;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.SandboxInteractor;
import judgels.gabriel.sandboxes.SandboxExecutor;

public class IwrapperSandboxInteractor implements SandboxInteractor {
    private final String iwrapperPath;

    public IwrapperSandboxInteractor(String iwrapperPath) {
        this.iwrapperPath = iwrapperPath;
    }

    @Override
    public SandboxExecutionResult[] interact(
            Sandbox sandbox1,
            List<String> command1,
            Sandbox sandbox2,
            List<String> command2) {

        ProcessBuilder pb1 = sandbox1.getProcessBuilder(command1);
        ProcessBuilder pb2 = sandbox2.getProcessBuilder(command2);

        ImmutableList.Builder<String> commandBuilder = ImmutableList.builder();

        commandBuilder.add(iwrapperPath);
        commandBuilder.addAll(pb1.command());
        commandBuilder.add("@@");
        commandBuilder.addAll(pb2.command());

        try {
            ProcessBuilder pb = new ProcessBuilder(commandBuilder.build());
            SandboxExecutor.executeProcessBuilder(pb);
        } catch (IOException | InterruptedException e) {
            return new SandboxExecutionResult[]{
                    SandboxExecutionResult.internalError(e.getMessage()),
                    SandboxExecutionResult.internalError(e.getMessage())
            };
        }

        return new SandboxExecutionResult[]{
                sandbox1.getResult(0),
                sandbox2.getResult(0)
        };
    }
}
