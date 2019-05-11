package judgels.gabriel.sandboxes;

import com.google.common.collect.ImmutableList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import judgels.gabriel.api.ProcessExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SandboxExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SandboxExecutor.class);

    private SandboxExecutor() {}

    public static ProcessExecutionResult executeProcessBuilder(ProcessBuilder pb)
            throws IOException, InterruptedException {

        Process process;

        LOGGER.info("Running {} ...", String.join(" ", pb.command()));
        process = pb.start();

        int exitCode = process.waitFor();
        LOGGER.info("    Exit code: {}", exitCode);

        ImmutableList.Builder<String> outputLines = ImmutableList.builder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            LOGGER.info("    Output: {}", line);
            outputLines.add(line);
        }

        return new ProcessExecutionResult.Builder()
                .exitCode(exitCode)
                .outputLines(outputLines.build())
                .build();
    }
}
