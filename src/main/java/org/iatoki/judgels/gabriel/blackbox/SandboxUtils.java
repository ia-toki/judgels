package org.iatoki.judgels.gabriel.blackbox;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.gabriel.GabrielLogger;
import org.iatoki.judgels.gabriel.blackbox.ProcessExecutionResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class SandboxUtils {
    private SandboxUtils() {
        // prevent instantiation
    }

    public static ProcessExecutionResult executeProcessBuilder(ProcessBuilder pb) throws IOException, InterruptedException {
        Process p;

        GabrielLogger.getLogger().info("Running {} ...", Joiner.on(" ").join(pb.command()));
        p = pb.start();

        int exitCode = p.waitFor();
        GabrielLogger.getLogger().info("    Exit code: {}", exitCode);

        ImmutableList.Builder<String> outputLines = ImmutableList.builder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            GabrielLogger.getLogger().info("    Output: {}", line);
            outputLines.add(line);
        }

        return new ProcessExecutionResult(exitCode, outputLines.build());
    }
}
