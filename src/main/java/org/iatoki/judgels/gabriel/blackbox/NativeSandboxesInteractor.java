package org.iatoki.judgels.gabriel.blackbox;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public final class NativeSandboxesInteractor implements SandboxesInteractor {
    @Override
    public SandboxExecutionResult[] runInteraction(Sandbox sandbox1, List<String> command1, Sandbox sandbox2, List<String> command2) {

        ProcessBuilder pb1 = sandbox1.getProcessBuilder(command1);
        ProcessBuilder pb2 = sandbox2.getProcessBuilder(command2);

        Process p1;
        Process p2;

        try {
            p1 = pb1.start();
            p2 = pb2.start();
        } catch (IOException e) {
            return new SandboxExecutionResult[]{
                    new SandboxExecutionResult(SandboxExecutionStatus.INTERNAL_ERROR, new SandboxExecutionResultDetails(-1, -1, -1, "")),
                    new SandboxExecutionResult(SandboxExecutionStatus.INTERNAL_ERROR, new SandboxExecutionResultDetails(-1, -1, -1, ""))
            };
        }

        InputStream p1InputStream = p1.getInputStream();
        OutputStream p1OutputStream = p1.getOutputStream();

        InputStream p2InputStream = p2.getInputStream();
        OutputStream p2OutputStream = p2.getOutputStream();

        while (true) {
            boolean success1 = transfer(p1InputStream, p2OutputStream);
            boolean success2 = transfer(p2InputStream, p1OutputStream);

            if (!success1 && !success2) {
                break;
            }
        }

        int exitCode1 = 0;
        int exitCode2 = 0;
        try {
            p1InputStream.close();
            p1OutputStream.close();
            p2InputStream.close();
            p2OutputStream.close();

            exitCode1 = p1.waitFor();
            exitCode2 = p2.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("AW");
        }

        return new SandboxExecutionResult[]{
                sandbox1.getResult(exitCode1),
                sandbox1.getResult(exitCode2)
        };
    }

    private boolean transfer(InputStream in, OutputStream out) {
        try {
            byte[] buffer = new byte[4096];
            int len = in.read(buffer);
            if (len == -1) {
                return false;
            }

            out.write(buffer, 0, len);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
