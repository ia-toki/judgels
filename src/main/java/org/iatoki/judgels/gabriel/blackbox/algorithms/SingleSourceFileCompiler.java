package org.iatoki.judgels.gabriel.blackbox.algorithms;

import com.google.common.base.Joiner;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.SandboxExecutionResult;
import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.CompilationException;
import org.iatoki.judgels.gabriel.blackbox.CompilationResult;
import org.iatoki.judgels.gabriel.blackbox.CompilationVerdict;
import org.iatoki.judgels.gabriel.SandboxExecutionStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class SingleSourceFileCompiler implements org.iatoki.judgels.gabriel.blackbox.Compiler {

    private final Sandbox sandbox;
    private final File compilationDir;

    private static final String COMPILATION_OUTPUT_FILENAME = "_compilation.out";

    private List<String> compilationCommand;
    private String executableFilename;

    public SingleSourceFileCompiler(Sandbox sandbox, File compilationDir, Language language, File sourceFile, int timeLimitInMilliseconds, int memoryLimitInKilobytes) {
        sandbox.addFile(sourceFile);
        sandbox.setTimeLimitInMilliseconds(timeLimitInMilliseconds);
        sandbox.setMemoryLimitInKilobytes(memoryLimitInKilobytes);
        sandbox.setStackSizeInKilobytes(memoryLimitInKilobytes);
        sandbox.setStandardOutput(COMPILATION_OUTPUT_FILENAME);
        sandbox.setStandardError(COMPILATION_OUTPUT_FILENAME);

        this.sandbox = sandbox;
        this.compilationDir = compilationDir;

        this.compilationCommand = language.getCompilationCommand(sourceFile.getName());
        this.executableFilename = language.getExecutableFilename(sourceFile.getName());
    }

    @Override
    public CompilationResult compile() throws CompilationException {
        SandboxExecutionResult executionResult = sandbox.execute(compilationCommand);

        if (executionResult.getStatus() == SandboxExecutionStatus.OK) {
            File compilationOutputFile = sandbox.getFile(COMPILATION_OUTPUT_FILENAME);
            try {
                String compilationOutput = FileUtils.readFileToString(compilationOutputFile);
                FileUtils.forceDelete(compilationOutputFile);
                FileUtils.copyFileToDirectory(sandbox.getFile(executableFilename), compilationDir);
                return new CompilationResult(CompilationVerdict.OK, compilationOutput);
            } catch (IOException e) {
                throw new CompilationException(e.getMessage());
            }

        } else if (executionResult.getStatus() == SandboxExecutionStatus.RUNTIME_ERROR) {
            File compilationOutputFile = sandbox.getFile(COMPILATION_OUTPUT_FILENAME);
            try {
                String compilationOutput = FileUtils.readFileToString(compilationOutputFile);
                FileUtils.forceDelete(compilationOutputFile);

                return new CompilationResult(CompilationVerdict.COMPILATION_ERROR, compilationOutput);
            } catch (IOException e) {
                throw new CompilationException(e.getMessage());
            }
        } else {
            throw new CompilationException(Joiner.on(" ").join(compilationCommand) + " resulted in " + executionResult.getDetails());
        }
    }
}
