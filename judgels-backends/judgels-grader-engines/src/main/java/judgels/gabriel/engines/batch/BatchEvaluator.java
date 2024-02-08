package judgels.gabriel.engines.batch;

import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import judgels.gabriel.api.EvaluationException;
import judgels.gabriel.api.EvaluationResult;
import judgels.gabriel.api.Evaluator;
import judgels.gabriel.api.GenerationException;
import judgels.gabriel.api.GenerationResult;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.SandboxExecutionStatus;
import judgels.gabriel.api.Scorer;
import judgels.gabriel.api.ScoringException;
import judgels.gabriel.api.ScoringResult;
import judgels.gabriel.api.TestCaseVerdict;
import judgels.gabriel.engines.FilePeeker;
import judgels.gabriel.helpers.TestCaseVerdictParser;
import org.apache.commons.io.FileUtils;

public class BatchEvaluator implements Evaluator {
    private static final String EVALUATION_OUTPUT_FILENAME = "_evaluation.out";

    private TestCaseVerdictParser verdictParser;
    private Scorer scorer;

    private Sandbox sandbox;

    private File compilationDir;
    private File evaluationDir;

    private String executableFilename;
    private List<String> solutionCommand;

    boolean shouldRevealEvaluation;

    public BatchEvaluator() {
        this.verdictParser = new TestCaseVerdictParser();
    }

    public void prepare(
            Sandbox sandbox,
            Scorer scorer,
            File compilationDir,
            File evaluationDir,
            GradingLanguage language,
            File sourceFile,
            int timeLimitInMilliseconds,
            int memoryLimitInKilobytes,
            boolean shouldRevealEvaluation) {

        sandbox.setTimeLimitInMilliseconds(timeLimitInMilliseconds);
        sandbox.setMemoryLimitInKilobytes(memoryLimitInKilobytes);

        this.verdictParser = new TestCaseVerdictParser();
        this.scorer = scorer;
        this.sandbox = sandbox;
        this.compilationDir = compilationDir;
        this.evaluationDir = evaluationDir;
        this.executableFilename = language.getExecutableFilename(sourceFile.getName());
        this.solutionCommand = language.getExecutionCommand(sourceFile.getName());
        this.shouldRevealEvaluation = shouldRevealEvaluation;
    }

    @Override
    public EvaluationResult evaluate(File input, File output) throws EvaluationException {
        if (!sandbox.containsFile(executableFilename)) {
            sandbox.addFile(new File(compilationDir, executableFilename));
            File executableFile = sandbox.getFile(executableFilename);
            if (!executableFile.setExecutable(true)) {
                throw new EvaluationException("Cannot set " + executableFile.getAbsolutePath() + " as executable");
            }
        }

        TestCaseVerdict verdict;
        GenerationResult generationResult = generate(input, output);
        if (generationResult.getVerdict().isPresent()) {
            verdict = generationResult.getVerdict().get();
        } else {
            ScoringResult scoringResult = score(input, output);
            verdict = scoringResult.getVerdict();
        }

        Optional<String> revealedInput = Optional.empty();
        if (shouldRevealEvaluation) {
            try {
                String evaluationInput = FilePeeker.peekInputOutput(input);
                revealedInput = Optional.of(evaluationInput);
            } catch (IOException e) {
                throw new EvaluationException(e);
            }
        }

        return new EvaluationResult.Builder()
                .verdict(verdict)
                .revealedInput(revealedInput)
                .revealedSolutionOutput(generationResult.getRevealedSolutionOutput())
                .executionResult(generationResult.getExecutionResult())
                .build();
    }

    @Override
    public GenerationResult generate(File input, File output) throws GenerationException {
        sandbox.addFile(input);
        sandbox.resetRedirections();
        sandbox.redirectStandardInput(input.getName());
        sandbox.redirectStandardOutput(EVALUATION_OUTPUT_FILENAME);

        Optional<String> revealedSolutionOutput = Optional.empty();
        SandboxExecutionResult result = sandbox.execute(solutionCommand);
        if (result.getStatus() == SandboxExecutionStatus.ZERO_EXIT_CODE) {
            File evaluationOutputFile;
            try {
                evaluationOutputFile = sandbox.getFile(EVALUATION_OUTPUT_FILENAME);
                FileUtils.copyFileToDirectory(evaluationOutputFile, evaluationDir);
            } catch (IOException e) {
                throw new GenerationException(e);
            }

            if (shouldRevealEvaluation) {
                try {
                    String evaluationOutput = FilePeeker.peekInputOutput(evaluationOutputFile);
                    revealedSolutionOutput = Optional.of(evaluationOutput);
                } catch (IOException e) {
                    throw new GenerationException(e);
                }
            }
        } else if (result.getStatus() == SandboxExecutionStatus.INTERNAL_ERROR) {
            throw new GenerationException(String.join(" ", solutionCommand) + " resulted in " + result);
        }

        sandbox.removeAllFilesExcept(ImmutableSet.of(executableFilename));

        return new GenerationResult.Builder()
                .verdict(verdictParser.parseExecutionResult(result))
                .revealedSolutionOutput(revealedSolutionOutput)
                .executionResult(result)
                .build();
    }

    @Override
    public ScoringResult score(File input, File output) throws ScoringException {
        return scorer.score(input, output, new File(evaluationDir, EVALUATION_OUTPUT_FILENAME));
    }
}
