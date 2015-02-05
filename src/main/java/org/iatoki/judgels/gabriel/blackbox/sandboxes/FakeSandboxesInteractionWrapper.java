package org.iatoki.judgels.gabriel.blackbox.sandboxes;

import org.iatoki.judgels.gabriel.blackbox.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionResult;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionResultDetails;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionStatus;
import org.iatoki.judgels.gabriel.blackbox.SandboxesInteractionWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class FakeSandboxesInteractionWrapper implements SandboxesInteractionWrapper {
    @Override
    public SandboxExecutionResult[] executeInteraction(Sandbox sandbox1, List<String> command1, Sandbox sandbox2, List<String> command2) {

        ProcessBuilder pb1 = sandbox1.getProcessBuilder(command1);
        ProcessBuilder pb2 = sandbox2.getProcessBuilder(command2);

        Process p1;
        Process p2;

        try {
            p1 = pb1.start();
            p2 = pb2.start();
        } catch (IOException e) {
            return new SandboxExecutionResult[]{
                    new SandboxExecutionResult(SandboxExecutionStatus.INTERNAL_ERROR, SandboxExecutionResultDetails.internalError(e.getMessage())),
                    new SandboxExecutionResult(SandboxExecutionStatus.INTERNAL_ERROR, SandboxExecutionResultDetails.internalError(e.getMessage()))
            };
        }

        InputStream p1InputStream = p1.getInputStream();
        OutputStream p1OutputStream = p1.getOutputStream();

        InputStream p2InputStream = p2.getInputStream();
        OutputStream p2OutputStream = p2.getOutputStream();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(new UnidirectionalPipe(p1InputStream, p2OutputStream));
        executor.submit(new UnidirectionalPipe(p2InputStream, p1OutputStream));

        int exitCode1 = 0;
        int exitCode2 = 0;
        try {
            exitCode2 = p2.waitFor();
            exitCode1 = p1.waitFor();
        } catch (InterruptedException e) {
            return new SandboxExecutionResult[]{
                    new SandboxExecutionResult(SandboxExecutionStatus.INTERNAL_ERROR, SandboxExecutionResultDetails.internalError(e.getMessage())),
                    new SandboxExecutionResult(SandboxExecutionStatus.INTERNAL_ERROR, SandboxExecutionResultDetails.internalError(e.getMessage()))
            };
        }

        return new SandboxExecutionResult[]{
                sandbox1.getResult(exitCode1),
                sandbox2.getResult(exitCode2)
        };
    }

}

class UnidirectionalPipe implements Runnable {
    private final InputStream in;
    private final OutputStream out;

    public UnidirectionalPipe(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] buffer = new byte[4096];
                int len = in.read(buffer);
                if (len == -1) {
                    break;
                }

                out.write(buffer, 0, len);
                out.flush();
            }

            in.close();
            out.close();

        } catch (IOException e) {

        }
    }
}