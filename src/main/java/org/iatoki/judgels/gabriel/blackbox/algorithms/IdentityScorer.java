package org.iatoki.judgels.gabriel.blackbox.algorithms;

import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.blackbox.Scorer;
import org.iatoki.judgels.gabriel.blackbox.ScoringException;
import org.iatoki.judgels.gabriel.blackbox.ScoringResult;
import org.iatoki.judgels.gabriel.blackbox.ScoringVerdict;

import java.io.File;
import java.io.IOException;

public final class IdentityScorer extends AbstractScorer {
    private final File evaluationDir;

    private static final String EVALUATION_OUTPUT_FILENAME = "_evaluation.out";

    public IdentityScorer(File evaluationDir) {
        this.evaluationDir = evaluationDir;
    }

    @Override
    protected String executeScoring(File testCaseInput, File testCaseOutput) throws ScoringException {
        try {
            File scoringOutputFile = new File(evaluationDir, EVALUATION_OUTPUT_FILENAME);
            return FileUtils.readFileToString(scoringOutputFile);
        } catch (IOException e) {
            throw new ScoringException(e.getMessage());
        }
    }
}
