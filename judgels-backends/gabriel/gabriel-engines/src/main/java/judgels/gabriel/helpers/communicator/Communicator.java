package judgels.gabriel.helpers.communicator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import judgels.gabriel.api.CompilationException;
import judgels.gabriel.api.CompilationResult;
import judgels.gabriel.api.EvaluationException;
import judgels.gabriel.api.EvaluationResult;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.SandboxExecutionStatus;
import judgels.gabriel.api.SandboxInteractor;
import judgels.gabriel.api.TestCaseVerdict;
import judgels.gabriel.compilers.SingleSourceFileCompiler;
import judgels.gabriel.helpers.TestCaseVerdictParser;
import judgels.gabriel.languages.cpp.Cpp11GradingLanguage;
import judgels.gabriel.languages.cpp.CppFamilyGradingLanguage;
import org.apache.commons.io.FileUtils;

public class Communicator {
    private static final String COMMUNICATION_OUTPUT_FILENAME = "_communication.out";

    private final SingleSourceFileCompiler compiler;
    private final TestCaseVerdictParser verdictParser;

    private Sandbox solutionSandbox;
    private Sandbox communicatorSandbox;
    private SandboxInteractor sandboxInteractor;

    private File compilationDir;
    private File communicationDir;

    private String solutionExecutableFilename;
    private String communicatorExecutableFilename;

    private List<String> solutionCommand;
    private List<String> communicationCommand;

    private CppFamilyGradingLanguage communicatorLanguage;

    public Communicator() {
        this.compiler = new SingleSourceFileCompiler();
        this.verdictParser = new TestCaseVerdictParser();
        this.communicatorLanguage = new Cpp11GradingLanguage();
    }

    public void prepare(
            Sandbox solutionSandbox,
            Sandbox communicatorSandbox,
            SandboxInteractor sandboxInteractor,
            File compilationDir,
            File communicationDir,
            GradingLanguage solutionLanguage,
            File solutionSourceFile,
            File communicatorSourceFile,
            int timeLimitInMilliseconds,
            int memoryLimitInKilobytes) throws PreparationException {

        compiler.prepare(communicatorSandbox, communicationDir, communicatorLanguage);

        CompilationResult result;
        try {
            result = compiler.compile(ImmutableMap.of("communicator", communicatorSourceFile));
        } catch (CompilationException e) {
            throw new PreparationException(e);
        }

        if (!result.isSuccessful()) {
            throw new PreparationException("Compilation of communicator resulted in compilation error:\n "
                    + result.getOutputs().get("communicator"));
        }

        this.solutionExecutableFilename =
                solutionLanguage.getExecutableFilename(solutionSourceFile.getName());
        this.communicatorExecutableFilename =
                communicatorLanguage.getExecutableFilename(communicatorSourceFile.getName());

        int wallTimeLimitInMilliseconds = timeLimitInMilliseconds + 2000;

        solutionSandbox.setTimeLimitInMilliseconds(timeLimitInMilliseconds);
        solutionSandbox.setWallTimeLimitInMilliseconds(wallTimeLimitInMilliseconds);
        solutionSandbox.setMemoryLimitInKilobytes(memoryLimitInKilobytes);

        communicatorSandbox.addFile(new File(communicationDir, communicatorExecutableFilename));
        File communicatorExecutableFile = communicatorSandbox.getFile(communicatorExecutableFilename);
        if (!communicatorExecutableFile.setExecutable(true)) {
            throw new PreparationException(
                    "Cannot set " + communicatorExecutableFile.getAbsolutePath() + " as executable");
        }

        communicatorSandbox.setTimeLimitInMilliseconds(timeLimitInMilliseconds);
        communicatorSandbox.setWallTimeLimitInMilliseconds(wallTimeLimitInMilliseconds);
        communicatorSandbox.setMemoryLimitInKilobytes(memoryLimitInKilobytes);

        solutionSandbox.addAllowedDirectory(communicationDir);
        communicatorSandbox.addAllowedDirectory(communicationDir);

        this.solutionSandbox = solutionSandbox;
        this.communicatorSandbox = communicatorSandbox;
        this.sandboxInteractor  = sandboxInteractor;

        this.compilationDir = compilationDir;
        this.communicationDir = communicationDir;

        this.solutionCommand = solutionLanguage.getExecutionCommand(solutionSourceFile.getName());
        this.communicationCommand = communicatorLanguage.getExecutionCommand(communicatorSourceFile.getName());
    }

    public EvaluationResult communicate(File input) throws EvaluationException {
        if (!solutionSandbox.containsFile(solutionExecutableFilename)) {
            solutionSandbox.addFile(new File(compilationDir, solutionExecutableFilename));
            File solutionExecutableFile = solutionSandbox.getFile(solutionExecutableFilename);
            if (!solutionExecutableFile.setExecutable(true)) {
                throw new EvaluationException(
                        "Cannot set " + solutionExecutableFile.getAbsolutePath() + " as executable");
            }
        }

        try {
            FileUtils.cleanDirectory(communicationDir);
        } catch (IOException e) {
            throw new EvaluationException(e);
        }

        communicatorSandbox.addFile(input);
        communicatorSandbox.resetRedirections();
        communicatorSandbox.redirectStandardError(COMMUNICATION_OUTPUT_FILENAME);

        List<String> command = ImmutableList.<String>builder()
                .addAll(communicationCommand)
                .add(input.getName())
                .build();

        SandboxExecutionResult[] results =
                sandboxInteractor.interact(solutionSandbox, solutionCommand, communicatorSandbox, command);

        SandboxExecutionResult solutionResult = ignoreSignal13(results[0]);
        SandboxExecutionResult communicatorResult = results[1];

        if (communicatorResult.getStatus() != SandboxExecutionStatus.ZERO_EXIT_CODE
                && communicatorResult.getStatus() != SandboxExecutionStatus.TIMED_OUT) {
            throw new EvaluationException(String.join(" ", command) + " resulted in " + communicatorResult);
        }

        SandboxExecutionResult finalResult;
        if (communicatorResult.getStatus() == SandboxExecutionStatus.TIMED_OUT) {
            finalResult = communicatorResult;
        } else {
            finalResult = solutionResult;
        }

        TestCaseVerdict verdict;

        Optional<TestCaseVerdict> maybeVerdict = verdictParser.parseExecutionResult(finalResult);
        if (maybeVerdict.isPresent()) {
            verdict = maybeVerdict.get();
        } else {
            String communicationOutput;
            try {
                File communicationOutputFile = communicatorSandbox.getFile(COMMUNICATION_OUTPUT_FILENAME);
                communicationOutput = FileUtils.readFileToString(communicationOutputFile, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new EvaluationException(e);
            }
            verdict = verdictParser.parseOutput(communicationOutput);
        }

        communicatorSandbox.removeAllFilesExcept(ImmutableSet.of(communicatorExecutableFilename));

        return new EvaluationResult.Builder()
                .verdict(verdict)
                .executionResult(finalResult)
                .build();
    }

    // Ignore errors caused by SIGPIPE (broken pipe); treat is as Wrong Answer / Accepted.
    private static SandboxExecutionResult ignoreSignal13(SandboxExecutionResult result) {
        if (result.getStatus() == SandboxExecutionStatus.KILLED_ON_SIGNAL
                && result.getMessage().orElse("").contains("Caught fatal signal 13")) {
            return new SandboxExecutionResult.Builder()
                    .from(result)
                    .status(SandboxExecutionStatus.ZERO_EXIT_CODE)
                    .message(Optional.empty())
                    .build();
        }
        return result;
    }
}
