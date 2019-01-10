package org.iatoki.judgels.gabriel.blackbox.algorithms;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.blackbox.EvaluationException;
import org.iatoki.judgels.gabriel.blackbox.EvaluationResult;
import org.iatoki.judgels.gabriel.blackbox.Evaluator;
import org.iatoki.judgels.gabriel.sandboxes.Sandbox;
import org.iatoki.judgels.gabriel.sandboxes.SandboxExecutionResult;
import org.iatoki.judgels.gabriel.sandboxes.SandboxExecutionStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FunctionalEvaluator implements Evaluator {
    private static final String EVALUATION_OUTPUT_FILENAME = "_evaluation.out";
    private static final String EXECUTABLE_FILENAME = "grader";

    private final Sandbox sandbox;
    private final File compilationDir;
    private final File evaluationDir;
    private final List<String> evaluationCommand;

    public FunctionalEvaluator(Sandbox sandbox, File compilationDir, File evaluationDir, int timeLimitInMilliseconds, int memoryLimitInKilobytes) {
        this.compilationDir = compilationDir;
        this.evaluationDir = evaluationDir;

        sandbox.setTimeLimitInMilliseconds(timeLimitInMilliseconds);
        sandbox.setMemoryLimitInKilobytes(memoryLimitInKilobytes);
        sandbox.setStackSizeInKilobytes(memoryLimitInKilobytes);

        this.sandbox = sandbox;
        this.evaluationCommand = ImmutableList.of("./" + EXECUTABLE_FILENAME);
    }

    @Override
    public EvaluationResult evaluate(File testCaseInputFile) throws EvaluationException {
        if (!sandbox.containsFile(EXECUTABLE_FILENAME)) {
            sandbox.addFile(new File(compilationDir, EXECUTABLE_FILENAME));
            File executableFile = sandbox.getFile(EXECUTABLE_FILENAME);
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

        sandbox.removeAllFilesExcept(ImmutableSet.of(EXECUTABLE_FILENAME));

        return EvaluationResult.executedResult(executionResult);
    }

    @Override
    public String getEvaluationResultFilename(File testcaseInputFile) {
        return EVALUATION_OUTPUT_FILENAME;
    }
}
