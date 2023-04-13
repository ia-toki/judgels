package judgels.gabriel.helpers.scorer;

import java.io.File;
import java.io.IOException;
import judgels.gabriel.api.Scorer;
import judgels.gabriel.api.ScoringException;
import judgels.gabriel.api.ScoringResult;
import judgels.gabriel.api.TestCaseVerdict;
import judgels.gabriel.api.Verdict;

public class DiffScorer implements Scorer {
    private static final String DIFF_COMMAND = "/usr/bin/diff --brief";
    private static final String TOKENIZER_COMMAND = " <(cat \"%s\" | tr '[\\t\\r\\n]' ' ' | xargs)";

    @Override
    public ScoringResult score(File input, File output, File evaluationOutput) throws ScoringException {
        String[] scoringCommand = new String[]{"bash", "-c", String.format(
                DIFF_COMMAND + TOKENIZER_COMMAND + TOKENIZER_COMMAND,
                output.getAbsolutePath(),
                evaluationOutput.getAbsolutePath())};

        ProcessBuilder pb = new ProcessBuilder(scoringCommand);
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
