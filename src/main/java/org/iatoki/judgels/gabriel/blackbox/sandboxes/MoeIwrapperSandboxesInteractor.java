package org.iatoki.judgels.gabriel.blackbox.sandboxes;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.gabriel.blackbox.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionResult;
import org.iatoki.judgels.gabriel.blackbox.SandboxesInteractor;

import java.io.IOException;
import java.util.List;

public class MoeIwrapperSandboxesInteractor implements SandboxesInteractor {
    private final String iwrapperPath;

    public MoeIwrapperSandboxesInteractor(String iwrapperPath) {
        this.iwrapperPath = iwrapperPath;
    }

    @Override
    public SandboxExecutionResult[] executeInteraction(Sandbox sandbox1, List<String> command1, Sandbox sandbox2, List<String> command2) {
        ProcessBuilder pb1 = sandbox1.getProcessBuilder(command1);
        ProcessBuilder pb2 = sandbox2.getProcessBuilder(command2);

        ImmutableList.Builder<String> commandBuilder = ImmutableList.builder();

        commandBuilder.add(iwrapperPath);
        commandBuilder.addAll(pb1.command());
        commandBuilder.add("@@");
        commandBuilder.addAll(pb2.command());

        try {
            new ProcessBuilder(commandBuilder.build()).start().waitFor();
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
