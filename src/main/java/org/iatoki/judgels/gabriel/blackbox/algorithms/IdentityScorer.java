package org.iatoki.judgels.gabriel.blackbox.algorithms;

import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.blackbox.ScoringException;

import java.io.File;
import java.io.IOException;

public final class IdentityScorer extends AbstractScorer {

    public IdentityScorer() {
    }

    @Override
    protected String executeScoring(File testCaseInput, File testCaseOutput, File scoringOutputFile) throws ScoringException {
        try {
            return FileUtils.readFileToString(scoringOutputFile);
        } catch (IOException e) {
            throw new ScoringException(e.getMessage());
        }
    }
}
