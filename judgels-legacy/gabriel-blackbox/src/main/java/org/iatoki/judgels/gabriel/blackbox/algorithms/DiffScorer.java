package org.iatoki.judgels.gabriel.blackbox.algorithms;

import judgels.gabriel.api.ScoringException;
import judgels.gabriel.api.ScoringResult;
import judgels.gabriel.api.TestCaseVerdict;
import judgels.gabriel.api.Verdict;
import org.iatoki.judgels.gabriel.blackbox.Scorer;

import java.io.File;
import java.io.IOException;

public final class DiffScorer implements Scorer {

    private static final String DIFF_EXECUTABLE_FILENAME = "/usr/bin/diff";

    @Override
    public ScoringResult score(File testCaseInput, File testCaseOutput, File evaluationOutputFile) throws ScoringException {
        String[] scoringCommand = new String[]{DIFF_EXECUTABLE_FILENAME, "--brief", testCaseOutput.getAbsolutePath(), evaluationOutputFile.getAbsolutePath()};
        ProcessBuilder pb = new ProcessBuilder(scoringCommand);

        int exitCode;
        try {
            exitCode = pb.start().waitFor();
        } catch (IOException | InterruptedException e) {
            throw new ScoringException(e.getMessage());
        }

        Verdict verdict = exitCode == 0 ? Verdict.ACCEPTED : Verdict.WRONG_ANSWER;
        return new ScoringResult.Builder()
                .verdict(new TestCaseVerdict.Builder().verdict(verdict).build())
                .build();
    }
}
