package org.iatoki.judgels.gabriel.steps;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.ExecutionResult;
import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.Verdict;
import org.iatoki.judgels.gabriel.blackbox.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class SimpleCompiler implements org.iatoki.judgels.gabriel.blackbox.Compiler {

    private final Sandbox sandbox;
    private final File tempDir;

    private static final String COMPILATION_OUTPUT_FILENAME = "_compilation.out";

    private List<String> compilationCommand;
    private String executableFilename;

    public SimpleCompiler(Sandbox sandbox, File tempDir, Language language, File sourceFile) {
        sandbox.addFile(sourceFile);
        sandbox.setTimeLimitInMilliseconds(10000);
        sandbox.setMemoryLimitInKilobytes(100 * 1024);
        sandbox.setStackSizeInKilobytes(100 * 1024);
        sandbox.setStandardOutput(COMPILATION_OUTPUT_FILENAME);
        sandbox.setStandardError(COMPILATION_OUTPUT_FILENAME);

        this.sandbox = sandbox;
        this.tempDir = tempDir;

        this.compilationCommand = language.getCompilationCommand(sourceFile.getName());
        this.executableFilename = language.getExecutableFilename(sourceFile.getName());
    }

    @Override
    public CompilationResult compile() throws CompilationException {
        ExecutionResult executionResult = sandbox.execute(compilationCommand);

        if (executionResult.getVerdict() == Verdict.OK) {

            File compilationOutputFile = sandbox.getFile(COMPILATION_OUTPUT_FILENAME);
            try {
                String compilationOutput = FileUtils.readFileToString(compilationOutputFile);

                FileUtils.copyFileToDirectory(sandbox.getFile(executableFilename), tempDir);
                File executableFile = new File(tempDir, executableFilename);
                if (!executableFile.setExecutable(true)) {
                    throw new CompilationException("Cannot set " + executableFile.getAbsolutePath() + " as executable");
                }

                Map<String, File> executableFiles = ImmutableMap.of(executableFilename, executableFile);

                return new CompilationResult(compilationOutput, executionResult, executableFiles);
            } catch (IOException e) {
                throw new CompilationException(e.getMessage());
            }

        } else if (executionResult.getVerdict() == Verdict.RUNTIME_ERROR) {
            File compilationOutputFile = sandbox.getFile(COMPILATION_OUTPUT_FILENAME);
            try {
                String compilationOutput = FileUtils.readFileToString(compilationOutputFile);
                throw new CompilationException(compilationOutput);
            } catch (IOException e) {
                throw new CompilationException(e.getMessage());
            }
        } else {
            throw new CompilationException("Compilation resulted in " + executionResult.getVerdict());
        }
    }
}
