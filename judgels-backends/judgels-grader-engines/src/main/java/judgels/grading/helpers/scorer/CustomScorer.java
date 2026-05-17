package judgels.grading.helpers.scorer;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import judgels.grading.api.CompilationException;
import judgels.grading.api.CompilationResult;
import judgels.grading.api.GradingLanguage;
import judgels.grading.api.PreparationException;
import judgels.grading.api.Sandbox;
import judgels.grading.api.SandboxExecutionResult;
import judgels.grading.api.SandboxExecutionStatus;
import judgels.grading.api.Scorer;
import judgels.grading.api.ScoringException;
import judgels.grading.api.ScoringResult;
import judgels.grading.compilers.SingleSourceFileCompiler;
import judgels.grading.helpers.TestCaseVerdictParser;
import org.apache.commons.io.FileUtils;

public class CustomScorer implements Scorer {
    private static final String SCORING_OUTPUT_FILENAME = "_scoring.out";

    private final SingleSourceFileCompiler compiler;
    private final TestCaseVerdictParser verdictParser;

    private Sandbox sandbox;

    private String executableFilename;
    private List<String> scoringCommand;

    public CustomScorer() {
        this.compiler = new SingleSourceFileCompiler();
        this.verdictParser = new TestCaseVerdictParser();
    }

    public void prepare(Sandbox sandbox, File scoringDir, GradingLanguage language, File scorerFile)
            throws PreparationException {

        compiler.prepare(sandbox, scoringDir, language);

        CompilationResult result;
        try {
            result = compiler.compile(ImmutableMap.of("scorer", scorerFile));
        } catch (CompilationException e) {
            throw new PreparationException(e);
        }

        if (!result.isSuccessful()) {
            throw new PreparationException("Compilation of custom scorer resulted in compilation error:\n "
                    + result.getOutputs().get("scorer"));
        }

        sandbox.setTimeLimitInMilliseconds(10 * 1000);
        sandbox.setMemoryLimitInKilobytes(1024 * 1024);

        this.sandbox = sandbox;
        this.executableFilename = language.getExecutableFilename(scorerFile.getName());
        this.scoringCommand = language.getExecutionCommand(scorerFile.getName());

        sandbox.addFile(new File(scoringDir, executableFilename));
        File executableFile = sandbox.getFile(executableFilename);
        if (!executableFile.setExecutable(true)) {
            throw new PreparationException("Cannot set " + executableFile.getAbsolutePath() + " as executable");
        }
    }

    @Override
    public ScoringResult score(File input, File output, File evaluationOutput) throws ScoringException {
        sandbox.addFile(evaluationOutput);
        sandbox.addFile(input);
        sandbox.addFile(output);
        sandbox.resetRedirections();
        sandbox.redirectStandardOutput(SCORING_OUTPUT_FILENAME);

        List<String> command = ImmutableList.<String>builder()
                .addAll(scoringCommand)
                .add(input.getName())
                .add(output.getName())
                .add(evaluationOutput.getName())
                .build();

        SandboxExecutionResult result = sandbox.execute(command);
        if (result.getStatus() != SandboxExecutionStatus.ZERO_EXIT_CODE) {
            throw new ScoringException(Joiner.on(" ").join(command) + " resulted in " + result);
        }

        String scoringOutput;
        try {
            File scoringOutputFile = sandbox.getFile(SCORING_OUTPUT_FILENAME);
            scoringOutput = FileUtils.readFileToString(scoringOutputFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ScoringException(e);
        }

        sandbox.removeAllFilesExcept(ImmutableSet.of(executableFilename));

        return new ScoringResult.Builder()
                .verdict(verdictParser.parseOutput(scoringOutput))
                .build();
    }
}
