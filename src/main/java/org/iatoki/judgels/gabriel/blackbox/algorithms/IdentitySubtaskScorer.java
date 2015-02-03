package org.iatoki.judgels.gabriel.blackbox.algorithms;

import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.blackbox.Scorer;
import org.iatoki.judgels.gabriel.blackbox.ScoringException;
import org.iatoki.judgels.gabriel.blackbox.ScoringResult;
import org.iatoki.judgels.gabriel.blackbox.ScoringVerdict;

import java.io.File;
import java.io.IOException;

public final class IdentitySubtaskScorer implements Scorer {
    private final File evaluationDir;

    private static final String EVALUATION_OUTPUT_FILENAME = "_evaluation.out";

    public IdentitySubtaskScorer(File evaluationDir) {
        this.evaluationDir = evaluationDir;
    }

    @Override
    public ScoringResult score(File testCaseInput, File testCaseOutput) throws ScoringException {
        String scoringOutput;
        try {
            File scoringOutputFile = new File(evaluationDir, EVALUATION_OUTPUT_FILENAME);
            scoringOutput = FileUtils.readFileToString(scoringOutputFile);
        } catch (IOException e) {
            throw new ScoringException(e.getMessage());
        }

        scoringOutput = scoringOutput.trim();

        switch (scoringOutput) {
            case "AC":
                return new ScoringResult(ScoringVerdict.ACCEPTED, "");
            case "WA":
                return new ScoringResult(ScoringVerdict.WRONG_ANSWER, "");
            default:
                throw new ScoringException("Unknown scoring format: " + scoringOutput);
        }
    }
}
