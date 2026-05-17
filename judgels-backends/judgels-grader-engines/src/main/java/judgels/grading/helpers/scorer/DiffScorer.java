package judgels.grading.helpers.scorer;

import java.io.File;
import java.io.IOException;
import judgels.grading.api.Scorer;
import judgels.grading.api.ScoringException;
import judgels.grading.api.ScoringResult;
import judgels.grading.api.TestCaseVerdict;
import judgels.grading.api.Verdict;

public class DiffScorer implements Scorer {
    private final File evaluationDir;

    public DiffScorer(File evaluationDir) {
        this.evaluationDir = evaluationDir;
    }

    @Override
    public ScoringResult score(File input, File output, File evaluationOutput) throws ScoringException {
        String[] scoringCommand = new String[]{"/bin/bash", "-c", String.format(""
                + "cat \"%s\" | tr '[\\t\\r\\n]' ' ' | xargs > _output_tokenized.out; "
                + "cat \"%s\" | tr '[\\t\\r\\n]' ' ' | xargs > _evaluation_tokenized.out; "
                + "diff --brief _output_tokenized.out _evaluation_tokenized.out; result=$?; "
                + "rm _output_tokenized.out _evaluation_tokenized.out; "
                + "exit $result",
                output.getAbsolutePath(),
                evaluationOutput.getAbsolutePath())};

        ProcessBuilder pb = new ProcessBuilder(scoringCommand);
        pb.directory(evaluationDir);

        int exitCode;
        try {
            exitCode = pb.start().waitFor();
        } catch (IOException | InterruptedException e) {
            throw new ScoringException(e);
        }

        Verdict verdict = exitCode == 0 ? Verdict.ACCEPTED : Verdict.WRONG_ANSWER;
        return new ScoringResult.Builder()
                .verdict(new TestCaseVerdict.Builder().verdict(verdict).build())
                .build();
    }
}
