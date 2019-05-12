package judgels.gabriel.compilers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import judgels.gabriel.api.CompilationException;
import judgels.gabriel.api.CompilationResult;
import judgels.gabriel.api.Compiler;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.SandboxExecutionStatus;
import org.apache.commons.io.FileUtils;

public class SingleSourceFileCompiler implements Compiler {
    private static final String COMPILATION_OUTPUT_FILENAME = "_compilation.out";

    private Sandbox sandbox;
    private File compilationDir;
    private GradingLanguage language;

    public void prepare(Sandbox sandbox, File compilationDir, GradingLanguage language) {
        sandbox.setTimeLimitInMilliseconds(10 * 1000);
        sandbox.setMemoryLimitInKilobytes(1024 * 1024);
        sandbox.setStackSizeInKilobytes(1024 * 1024);

        sandbox.resetRedirections();
        sandbox.redirectStandardOutput(COMPILATION_OUTPUT_FILENAME);
        sandbox.redirectStandardError(COMPILATION_OUTPUT_FILENAME);

        this.sandbox = sandbox;
        this.compilationDir = compilationDir;
        this.language = language;
    }

    @Override
    public CompilationResult compile(Map<String, File> sourceFiles) throws CompilationException {
        String sourceKey = sourceFiles.keySet().iterator().next();
        File sourceFile = sourceFiles.get(sourceKey);

        List<String> command = language.getCompilationCommand(sourceFile.getName());
        String executableFilename = language.getExecutableFilename(sourceFile.getName());

        sandbox.addFile(sourceFile);
        SandboxExecutionResult result = sandbox.execute(command);

        if (result.getStatus() == SandboxExecutionStatus.ZERO_EXIT_CODE) {
            File outputFile = sandbox.getFile(COMPILATION_OUTPUT_FILENAME);
            try {
                String compilationOutput = FileUtils.readFileToString(outputFile, StandardCharsets.UTF_8);
                FileUtils.forceDelete(outputFile);
                FileUtils.copyFileToDirectory(sandbox.getFile(executableFilename), compilationDir);

                return new CompilationResult.Builder()
                        .isSuccessful(true)
                        .putOutputs(sourceKey, compilationOutput)
                        .build();
            } catch (IOException e) {
                throw new CompilationException(e);
            }

        } else if (result.getStatus() == SandboxExecutionStatus.NONZERO_EXIT_CODE) {
            File outputFile = sandbox.getFile(COMPILATION_OUTPUT_FILENAME);
            try {
                String compilationOutput = FileUtils.readFileToString(outputFile, StandardCharsets.UTF_8);
                FileUtils.forceDelete(outputFile);

                return new CompilationResult.Builder()
                        .isSuccessful(false)
                        .putOutputs(sourceKey, compilationOutput)
                        .build();
            } catch (IOException e) {
                throw new CompilationException(e);
            }
        } else {
            throw new CompilationException(String.join(" ", command) + " resulted in " + result);
        }
    }
}
