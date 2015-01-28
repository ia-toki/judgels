package org.iatoki.judgels.gabriel.blackbox.algorithms;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.SandboxExecutionResult;
import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.EvaluationException;
import org.iatoki.judgels.gabriel.blackbox.EvaluationResult;
import org.iatoki.judgels.gabriel.blackbox.Evaluator;
import org.iatoki.judgels.gabriel.SandboxExecutionStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public final class BatchEvaluator implements Evaluator {
    private final Sandbox sandbox;
    private final File compilationDir;
    private final File evaluationDir;

    private final String executableFilename;

    private static final String EVALUATION_OUTPUT_FILENAME = "_evaluation.out";

    private final List<String> evaluationCommand;

    public BatchEvaluator(Sandbox sandbox, File compilationDir, File evaluationDir, Language language, File sourceFile, int timeLimitInMilliseconds, int memoryLimitInKilobytes) {
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
    public EvaluationResult evaluate(File testCaseInputFile, Set<Integer> subtaskNumbers) throws EvaluationException {
        if (!sandbox.containsFile(executableFilename)) {
            sandbox.addFile(new File(compilationDir, executableFilename));
        }

        sandbox.addFile(testCaseInputFile);
        sandbox.setStandardInput(testCaseInputFile.getName());
        sandbox.setStandardOutput(EVALUATION_OUTPUT_FILENAME);
        sandbox.setStandardError(EVALUATION_OUTPUT_FILENAME);

        SandboxExecutionResult executionResult = sandbox.execute(evaluationCommand);

        if (executionResult.getStatus() == SandboxExecutionStatus.OK) {
            try {
                FileUtils.copyFileToDirectory(sandbox.getFile(EVALUATION_OUTPUT_FILENAME), evaluationDir);
            } catch (IOException e) {
                throw new EvaluationException(e.getMessage());
            }
        }

        sandbox.removeAllFilesExcept(ImmutableSet.of(executableFilename));

        return EvaluationResult.executedResult(executionResult);
    }
}
