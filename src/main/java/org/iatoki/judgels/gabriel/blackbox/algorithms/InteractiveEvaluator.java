package org.iatoki.judgels.gabriel.blackbox.algorithms;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.GradingLanguage;
import org.iatoki.judgels.gabriel.blackbox.NativeSandboxesInteractor;
import org.iatoki.judgels.gabriel.blackbox.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionResult;
import org.iatoki.judgels.gabriel.blackbox.SandboxExecutionStatus;
import org.iatoki.judgels.gabriel.blackbox.SandboxesInteractor;
import org.iatoki.judgels.gabriel.blackbox.CompilationException;
import org.iatoki.judgels.gabriel.blackbox.CompilationResult;
import org.iatoki.judgels.gabriel.blackbox.CompilationVerdict;
import org.iatoki.judgels.gabriel.blackbox.EvaluationException;
import org.iatoki.judgels.gabriel.blackbox.EvaluationResult;
import org.iatoki.judgels.gabriel.blackbox.Evaluator;
import org.iatoki.judgels.gabriel.blackbox.PreparationException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public final class InteractiveEvaluator implements Evaluator {
    private final Sandbox contestantSandbox;
    private final Sandbox communicatorSandbox;

    private final File compilationDir;
    private final File evaluationDir;

    private final String contestantExecutableName;
    private final String communicatorExecutableName;

    private static final String EVALUATION_OUTPUT_FILENAME = "_evaluation.out";

    private final List<String> contestantEvaluationCommand;
    private final List<String> communicatorExecutionCommand;

    private final int evaluationTimeLimitInMilliseconds;

    public InteractiveEvaluator(Sandbox contestantSandbox, Sandbox communicatorSandbox, File compilationDir, File evaluationDir, GradingLanguage contestantLanguage, GradingLanguage communicatorLanguage, File contestantSourceFile, File communicatorSourceFile, int compilationTimeLimitInMilliseconds, int compilationMemoryLimitInKilobytes, int evaluationTimeLimitInMilliseconds, int evaluationMemoryLimitInMilliseconds) throws PreparationException {
        try {
            SingleSourceFileCompiler compiler = new SingleSourceFileCompiler(communicatorSandbox, evaluationDir, communicatorLanguage, "comunicator", communicatorSourceFile, compilationTimeLimitInMilliseconds, compilationMemoryLimitInKilobytes);
            CompilationResult result = compiler.compile();
            if (result.getVerdict() == CompilationVerdict.COMPILATION_ERROR) {
                throw new PreparationException("Compilation of the communicator resulted in compilation error:\n " + result.getOutputs().get("comumunicator"));
            }
        } catch (CompilationException e) {
            throw new PreparationException(e.getMessage());
        }

        this.compilationDir = compilationDir;
        this.evaluationDir = evaluationDir;
        this.contestantExecutableName = contestantLanguage.getExecutableFilename(contestantSourceFile.getName());
        this.communicatorExecutableName = communicatorLanguage.getExecutableFilename(communicatorSourceFile.getName());

        contestantSandbox.setTimeLimitInMilliseconds(evaluationTimeLimitInMilliseconds);
        contestantSandbox.setMemoryLimitInKilobytes(evaluationTimeLimitInMilliseconds);
        contestantSandbox.setStackSizeInKilobytes(evaluationMemoryLimitInMilliseconds);

        communicatorSandbox.addFile(new File(evaluationDir, communicatorExecutableName));
        File communicatorExecutableFile = communicatorSandbox.getFile(communicatorExecutableName);
        if (!communicatorExecutableFile.setExecutable(true)) {
            throw new PreparationException("Cannot set " + communicatorExecutableFile.getAbsolutePath() + " as executable");
        }

        communicatorSandbox.setTimeLimitInMilliseconds(evaluationTimeLimitInMilliseconds);
        communicatorSandbox.setMemoryLimitInKilobytes(evaluationTimeLimitInMilliseconds);
        communicatorSandbox.setStackSizeInKilobytes(evaluationMemoryLimitInMilliseconds);

        contestantSandbox.addAllowedDirectory(evaluationDir);
        communicatorSandbox.addAllowedDirectory(evaluationDir);

        this.contestantSandbox = contestantSandbox;
        this.communicatorSandbox = communicatorSandbox;

        this.contestantEvaluationCommand = contestantLanguage.getExecutionCommand(contestantSourceFile.getName());
        this.communicatorExecutionCommand = communicatorLanguage.getExecutionCommand(communicatorSourceFile.getName());

        this.evaluationTimeLimitInMilliseconds = evaluationTimeLimitInMilliseconds;
    }

    @Override
    public EvaluationResult evaluate(File testCaseInput, Set<Integer> subtaskNumbers) throws EvaluationException {
        if (!contestantSandbox.containsFile(contestantExecutableName)) {
            contestantSandbox.addFile(new File(compilationDir, contestantExecutableName));
            File contestantExecutableFile = contestantSandbox.getFile(contestantExecutableName);
            if (!contestantExecutableFile.setExecutable(true)) {
                throw new EvaluationException("Cannot set " + contestantExecutableFile.getAbsolutePath() + " as executable");
            }
        }

        try {
            FileUtils.cleanDirectory(evaluationDir);
        } catch (IOException e) {
            throw new EvaluationException(e.getMessage());
        }

        File pipe1 = new File(evaluationDir, "pipe1");
        File pipe2 = new File(evaluationDir, "pipe2");

        int exitCode1, exitCode2;
        try {
            exitCode1 = new ProcessBuilder(new String[]{"/usr/bin/mkfifo", pipe1.getAbsolutePath()}).start().waitFor();
            exitCode2 = new ProcessBuilder(new String[]{"/usr/bin/mkfifo", pipe2.getAbsolutePath()}).start().waitFor();
        } catch (IOException | InterruptedException e) {
            throw new EvaluationException(e.getMessage());
        }

        if (exitCode1 != 0 || exitCode2 != 0) {
            throw new EvaluationException("Cannot create pipes in " + evaluationDir.getAbsolutePath() + " for evaluation");
        }

        communicatorSandbox.setStandardError(EVALUATION_OUTPUT_FILENAME);

        ImmutableList.Builder<String> communicatorEvaluationCommandBuilder = ImmutableList.builder();
        communicatorEvaluationCommandBuilder.addAll(communicatorExecutionCommand);
        communicatorEvaluationCommandBuilder.add(testCaseInput.getName());

        List<String> communicatorEvaluationCommand = communicatorEvaluationCommandBuilder.build();

        SandboxesInteractor interactor = new NativeSandboxesInteractor();

        SandboxExecutionResult[] results = interactor.runInteraction(communicatorSandbox, communicatorExecutionCommand, contestantSandbox, contestantEvaluationCommand);

        SandboxExecutionResult communicatorExecutionResult = results[0];
        SandboxExecutionResult contestantExecutionResult = results[1];


        // Note that if communicator resulted in TLE, it is impossible to tell whether it is communicator's fault or contestant's.
        // Just return TLE anyway.

        if (communicatorExecutionResult.getStatus() != SandboxExecutionStatus.OK && communicatorExecutionResult.getStatus() != SandboxExecutionStatus.TIME_LIMIT_EXCEEDED) {
            throw new EvaluationException(Joiner.on(" ").join(communicatorEvaluationCommand) + " resulted in " + communicatorExecutionResult.getDetails());
        }

        try {
            FileUtils.copyFileToDirectory(communicatorSandbox.getFile(EVALUATION_OUTPUT_FILENAME), evaluationDir);
        } catch (IOException e) {
            throw new EvaluationException(e.getMessage());
        }

        communicatorSandbox.removeAllFilesExcept(ImmutableSet.of(communicatorExecutableName));

        return EvaluationResult.executedResult(contestantExecutionResult);
    }
}
