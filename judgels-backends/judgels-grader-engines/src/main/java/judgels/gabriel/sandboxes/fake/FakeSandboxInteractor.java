package judgels.gabriel.sandboxes.fake;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxException;
import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.SandboxExecutionStatus;
import judgels.gabriel.api.SandboxInteractor;

public class FakeSandboxInteractor implements SandboxInteractor {
    @Override
    public SandboxExecutionResult[] interact(
            Sandbox sandbox1,
            List<String> command1,
            Sandbox sandbox2,
            List<String> command2) {

        ProcessBuilder pb1 = sandbox1.getProcessBuilder(command1);
        ProcessBuilder pb2 = sandbox2.getProcessBuilder(command2);

        Process p1;
        Process p2;

        try {
            p1 = pb1.start();
            p2 = pb2.start();
        } catch (IOException e) {
            return new SandboxExecutionResult[]{
                    SandboxExecutionResult.internalError(e.getMessage()),
                    SandboxExecutionResult.internalError(e.getMessage())
            };
        }

        InputStream p1InputStream = p1.getInputStream();
        OutputStream p1OutputStream = p1.getOutputStream();

        InputStream p2InputStream = p2.getInputStream();
        OutputStream p2OutputStream = p2.getOutputStream();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        UnidirectionalPipe pipe1 = new UnidirectionalPipe(p1InputStream, p2OutputStream);
        UnidirectionalPipe pipe2 = new UnidirectionalPipe(p2InputStream, p1OutputStream);

        executor.submit(pipe1);
        executor.submit(pipe2);

        int exitCode1;
        int exitCode2;
        try {
            exitCode2 = p2.waitFor();
            exitCode1 = p1.waitFor();
        } catch (InterruptedException e) {
            return new SandboxExecutionResult[]{
                    SandboxExecutionResult.internalError(e.getMessage()),
                    SandboxExecutionResult.internalError(e.getMessage())
            };
        }

        SandboxExecutionResult result1 = sandbox1.getResult(exitCode1);
        SandboxExecutionResult result2 = sandbox2.getResult(exitCode2);

        if (pipe1.receivedSignal13) {
            result1 = newKilledOnSignal13Result(result1);
        }
        if (pipe2.receivedSignal13) {
            result2 = newKilledOnSignal13Result(result2);
        }

        return new SandboxExecutionResult[]{result1, result2};
    }

    private static SandboxExecutionResult newKilledOnSignal13Result(SandboxExecutionResult result) {
        return new SandboxExecutionResult.Builder()
                .from(result)
                .status(SandboxExecutionStatus.KILLED_ON_SIGNAL)
                .exitSignal(13)
                .isKilled(true)
                .message("Caught fatal signal 13")
                .build();
    }

    class UnidirectionalPipe implements Runnable {
        private final InputStream in;
        private final OutputStream out;

        boolean receivedSignal13;

        UnidirectionalPipe(InputStream in, OutputStream out) {
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
                if (e.getMessage().equals("Stream closed")) {
                    receivedSignal13 = true;
                } else {
                    throw new SandboxException(e);
                }
            }
        }
    }
}
