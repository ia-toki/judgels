package org.iatoki.judgels.gabriel.steps;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.ExecutionResult;
import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.Verdict;
import org.iatoki.judgels.gabriel.blackbox.EvaluationException;
import org.iatoki.judgels.gabriel.blackbox.EvaluationResult;
import org.iatoki.judgels.gabriel.blackbox.Evaluator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class SimpleEvaluator implements Evaluator {
    private final Sandbox sandbox;
    private final File tempDir;

    private static final String EVALUATION_OUTPUT_FILENAME = "_evaluation.out";

    private final String executableFilename;
    private final List<String> evaluationCommand;

    public SimpleEvaluator(Sandbox sandbox, File tempDir, Language language, File sourceFile, int timeLimitInMilliseconds, int memoryLimitInKilobytes) {
        sandbox.setTimeLimitInMilliseconds(timeLimitInMilliseconds);
        sandbox.setMemoryLimitInKilobytes(memoryLimitInKilobytes);
        sandbox.setStackSizeInKilobytes(memoryLimitInKilobytes);

        this.sandbox = sandbox;
        this.tempDir = tempDir;

        this.executableFilename = language.getExecutableFilename(sourceFile.getName());
        this.evaluationCommand = language.getExecutionCommand(sourceFile.getName());
    }

    @Override
    public EvaluationResult evaluate(Map<String, File> executableFiles, File testCaseInputFile) throws EvaluationException {

        if (!sandbox.containsFile(executableFilename)) {
            sandbox.addFile(executableFiles.get(executableFilename));
        }

        sandbox.addFile(testCaseInputFile);
        sandbox.setStandardInput(testCaseInputFile.getName());
        sandbox.setStandardOutput(EVALUATION_OUTPUT_FILENAME);
        sandbox.setStandardError(EVALUATION_OUTPUT_FILENAME);

        ExecutionResult executionResult = sandbox.execute(evaluationCommand);
        Map<String, File> evaluationOutputFiles;

        if (executionResult.getVerdict() == Verdict.OK) {

            try {
                FileUtils.copyFileToDirectory(sandbox.getFile(EVALUATION_OUTPUT_FILENAME), tempDir);
            } catch (IOException e) {
                throw new EvaluationException(e.getMessage());
            }
            File evaluationOutputFile = new File(tempDir, EVALUATION_OUTPUT_FILENAME);
            evaluationOutputFiles = ImmutableMap.of(EVALUATION_OUTPUT_FILENAME, evaluationOutputFile);
        } else {
            evaluationOutputFiles = ImmutableMap.of();
        }
        return new EvaluationResult(executionResult, evaluationOutputFiles);
    }

}
