package org.iatoki.judgels.gabriel.blackbox.algorithms;

import org.iatoki.judgels.gabriel.blackbox.Scorer;
import org.iatoki.judgels.gabriel.blackbox.ScoringException;
import org.iatoki.judgels.gabriel.blackbox.ScoringResult;
import org.iatoki.judgels.gabriel.blackbox.ScoringVerdict;

import java.io.File;
import java.io.IOException;

public final class DiffScorer implements Scorer {
    private final File evaluationDir;

    private static final String EVALUATION_OUTPUT_FILENAME = "_evaluation.out";
    private static final String DIFF_EXECUTABLE_FILENAME = "/usr/bin/diff";

    public DiffScorer(File evaluationDir) {
        this.evaluationDir = evaluationDir;
    }

    @Override
    public ScoringResult score(File testCaseInput, File testCaseOutput) throws ScoringException {
        File evaluationOutputFile = new File(evaluationDir, EVALUATION_OUTPUT_FILENAME);

        String[] scoringCommand = new String[]{DIFF_EXECUTABLE_FILENAME, "--brief", testCaseOutput.getAbsolutePath(), evaluationOutputFile.getAbsolutePath()};
        ProcessBuilder pb = new ProcessBuilder(scoringCommand);

        int exitCode;
        try {
            exitCode = pb.start().waitFor();
        } catch (IOException | InterruptedException e) {
            throw new ScoringException(e.getMessage());
        }

        if (exitCode == 0) {
            return new ScoringResult(ScoringVerdict.ACCEPTED, "");
        } else {
            return new ScoringResult(ScoringVerdict.WRONG_ANSWER, "");
        }
    }
}
