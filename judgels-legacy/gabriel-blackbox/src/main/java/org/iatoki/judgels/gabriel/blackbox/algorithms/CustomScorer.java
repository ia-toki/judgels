package org.iatoki.judgels.gabriel.blackbox.algorithms;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import judgels.gabriel.api.CompilationException;
import judgels.gabriel.api.CompilationResult;
import judgels.gabriel.api.Compiler;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.SandboxExecutionStatus;
import judgels.gabriel.api.ScoringException;
import judgels.gabriel.api.Verdict;
import judgels.gabriel.compilers.SingleSourceFileCompiler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class CustomScorer extends AbstractScorer {

    private static final String SCORING_OUTPUT_FILENAME = "_scoring.out";

    private final Compiler scorerCompiler;
    private final Sandbox sandbox;
    private final String scorerExecutableFilename;
    private final List<String> customScorerExecutionCommand;

    public CustomScorer(Sandbox sandbox, File scoringDir, GradingLanguage language, File scorerFile, int compilationTimeLimitInMilliseconds, int compilationMemoryLimitInKilobytes, int scoringTimeLimitInMilliseconds, int scoringMemoryLimitInKilobytes) throws PreparationException {
        scorerCompiler = new SingleSourceFileCompiler();
        scorerCompiler.prepare(sandbox, scoringDir, language, ImmutableMap.of(), compilationTimeLimitInMilliseconds, compilationMemoryLimitInKilobytes);
        try {
            CompilationResult result = scorerCompiler.compile(ImmutableMap.of("scorer", scorerFile));

            if (result.getVerdict() == Verdict.COMPILATION_ERROR) {
                throw new PreparationException("Compilation of custom scorer resulted in compilation error:\n " + result.getOutputs().get("scorer"));
            }
        } catch (CompilationException e) {
            throw new PreparationException(e);
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
        this.customScorerExecutionCommand = language.getExecutionCommand(scorerFile.getName());
    }


    @Override
    public String executeScoring(File testCaseInput, File testCaseOutput, File evaluationOutputFile) throws ScoringException {
        sandbox.addFile(evaluationOutputFile);
        sandbox.addFile(testCaseInput);
        sandbox.addFile(testCaseOutput);

        ImmutableList.Builder<String> scoringCommandBuilder = ImmutableList.builder();
        scoringCommandBuilder.addAll(customScorerExecutionCommand);
        scoringCommandBuilder.add(testCaseInput.getName());
        scoringCommandBuilder.add(testCaseOutput.getName());
        scoringCommandBuilder.add(evaluationOutputFile.getName());

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
