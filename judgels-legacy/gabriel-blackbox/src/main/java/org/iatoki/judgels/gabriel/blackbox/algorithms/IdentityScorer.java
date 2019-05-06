package org.iatoki.judgels.gabriel.blackbox.algorithms;

import judgels.gabriel.api.ScoringException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public final class IdentityScorer extends AbstractScorer {

    @Override
    protected String executeScoring(File testCaseInput, File testCaseOutput, File scoringOutputFile) throws ScoringException {
        try {
            return FileUtils.readFileToString(scoringOutputFile);
        } catch (IOException e) {
            throw new ScoringException(e.getMessage());
        }
    }
}
