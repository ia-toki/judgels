package judgels.gabriel.helpers.scorer;

import java.io.File;
import java.io.IOException;
import judgels.gabriel.api.Scorer;
import judgels.gabriel.api.ScoringException;
import judgels.gabriel.api.ScoringResult;
import judgels.gabriel.api.TestCaseVerdict;
import judgels.gabriel.api.Verdict;

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
