package org.iatoki.judgels.gabriel.blackbox.algorithms;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionResult;
import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.blackbox.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.EvaluationException;
import org.iatoki.judgels.gabriel.blackbox.EvaluationResult;
import org.iatoki.judgels.gabriel.blackbox.Evaluator;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class BatchEvaluator implements Evaluator {
    private final Sandbox sandbox;
    private final File compilationDir;
    private final File evaluationDir;

    private final String executableFilename;

    private static final String EVALUATION_OUTPUT_FILENAME = "_evaluation.out";

    private final List<String> evaluationCommand;

    public BatchEvaluator(Sandbox sandbox, File compilationDir, File evaluationDir, GradingLanguage language, File sourceFile, int timeLimitInMilliseconds, int memoryLimitInKilobytes) {
        this.compilationDir = compilationDir;
        this.evaluationDir = evaluationDir;
        this.executableFilename = language.getExecutableFilename(sourceFile.getName());

        sandbox.setTimeLimitInMilliseconds(timeLimitInMilliseconds);
        sandbox.setMemoryLimitInKilobytes(memoryLimitInKilobytes);
        sandbox.setStackSizeInKilobytes(memoryLimitInKilobytes);

        this.sandbox = sandbox;
        this.evaluationCommand = language.getExecutionCommand(sourceFile.getName());
    }

    @Override
    public EvaluationResult evaluate(File testCaseInputFile) throws EvaluationException {
        if (!sandbox.containsFile(executableFilename)) {
            sandbox.addFile(new File(compilationDir, executableFilename));
            File executableFile = sandbox.getFile(executableFilename);
            if (!executableFile.setExecutable(true)) {
                throw new EvaluationException("Cannot set " + executableFile.getAbsolutePath() + " as executable");
            }
        }

        sandbox.addFile(testCaseInputFile);

        sandbox.resetRedirections();
        sandbox.redirectStandardInput(testCaseInputFile.getName());
        sandbox.redirectStandardOutput(EVALUATION_OUTPUT_FILENAME);
        sandbox.redirectStandardError(EVALUATION_OUTPUT_FILENAME);

        SandboxExecutionResult executionResult = sandbox.execute(evaluationCommand);

        if (executionResult.getStatus() == SandboxExecutionStatus.ZERO_EXIT_CODE) {
            try {
                FileUtils.copyFileToDirectory(sandbox.getFile(EVALUATION_OUTPUT_FILENAME), evaluationDir);
            } catch (IOException e) {
                throw new EvaluationException(e.getMessage());
            }
        } else if (executionResult.getStatus() == SandboxExecutionStatus.INTERNAL_ERROR) {
            throw new EvaluationException(Joiner.on(" ").join(evaluationCommand) + " resulted in " + executionResult);
        }

        sandbox.removeAllFilesExcept(ImmutableSet.of(executableFilename));

        return EvaluationResult.executedResult(executionResult);
    }
}