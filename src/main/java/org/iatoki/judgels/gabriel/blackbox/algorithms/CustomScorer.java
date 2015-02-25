package org.iatoki.judgels.gabriel.blackbox.algorithms;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionResult;
import org.iatoki.judgels.gabriel.blackbox.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionStatus;
import org.iatoki.judgels.gabriel.blackbox.CompilationException;
import org.iatoki.judgels.gabriel.blackbox.CompilationResult;
import org.iatoki.judgels.gabriel.blackbox.CompilationVerdict;
import org.iatoki.judgels.gabriel.blackbox.PreparationException;
import org.iatoki.judgels.gabriel.blackbox.ScoringException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class CustomScorer extends AbstractScorer {
    private final Sandbox sandbox;
    private final File evaluationDir;
    private final String scorerExecutableFilename;
    private final List<String> customScorerExecutionCommand;

    private static final String EVALUATION_OUTPUT_FILENAME = "_evaluation.out";
    private static final String SCORING_OUTPUT_FILENAME = "_scoring.out";

    public CustomScorer(Sandbox sandbox, File evaluationDir, File scoringDir, GradingLanguage language, File scorerFile, int compilationTimeLimitInMilliseconds, int compilationMemoryLimitInKilobytes, int scoringTimeLimitInMilliseconds, int scoringMemoryLimitInKilobytes) throws PreparationException {
        try {
            SingleSourceFileCompiler compiler = new SingleSourceFileCompiler(sandbox, scoringDir, language, "customScorer", scorerFile, compilationTimeLimitInMilliseconds, compilationMemoryLimitInKilobytes);
            CompilationResult result = compiler.compile();
            if (result.getVerdict() == CompilationVerdict.COMPILATION_ERROR) {
                throw new PreparationException("Compilation of custom scorer resulted in compilation error:\n " + result.getOutputs().get("customScorer"));
            }
        } catch (CompilationException e) {
            throw new PreparationException(e.getMessage());
        }

        this.scorerExecutableFilename = language.getExecutableFilename(scorerFile.getName());
        sandbox.addFile(new File(scoringDir, scorerExecutableFilename));
        File scorerExecutableFile = sandbox.getFile(scorerExecutableFilename);
        if (!scorerExecutableFile.setExecutable(true)) {
            throw new PreparationException("Cannot set " + scorerExecutableFile.getAbsolutePath() + " as executable");
        }
        sandbox.setTimeLimitInMilliseconds(scoringTimeLimitInMilliseconds);
        sandbox.setMemoryLimitInKilobytes(scoringMemoryLimitInKilobytes);

        this.sandbox = sandbox;
        this.evaluationDir = evaluationDir;
        this.customScorerExecutionCommand = language.getExecutionCommand(scorerFile.getName());
    }

    @Override
    protected String executeScoring(File testCaseInput, File testCaseOutput) throws ScoringException {
        sandbox.addFile(new File(evaluationDir, EVALUATION_OUTPUT_FILENAME));
        sandbox.addFile(testCaseInput);
        sandbox.addFile(testCaseOutput);

        ImmutableList.Builder<String> scoringCommandBuilder = ImmutableList.builder();
        scoringCommandBuilder.addAll(customScorerExecutionCommand);
        scoringCommandBuilder.add(testCaseInput.getName());
        scoringCommandBuilder.add(testCaseOutput.getName());
        scoringCommandBuilder.add(EVALUATION_OUTPUT_FILENAME);

        sandbox.redirectStandardOutput(SCORING_OUTPUT_FILENAME);

        List<String> scoringCommand = scoringCommandBuilder.build();
        SandboxExecutionResult executionResult = sandbox.execute(scoringCommand);

        if (executionResult.getStatus() != SandboxExecutionStatus.ZERO_EXIT_CODE) {
            throw new ScoringException(Joiner.on(" ").join(scoringCommand) + " resulted in " + executionResult);
        }

        String scoringOutput;
        try {
            File scoringOutputFile = sandbox.getFile(SCORING_OUTPUT_FILENAME);
            scoringOutput = FileUtils.readFileToString(scoringOutputFile);
        } catch (IOException e) {
            throw new ScoringException(e.getMessage());
        }

        sandbox.removeAllFilesExcept(ImmutableSet.of(scorerExecutableFilename));

        return scoringOutput;
    }
}