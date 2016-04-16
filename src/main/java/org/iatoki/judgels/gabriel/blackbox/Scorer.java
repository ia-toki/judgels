package org.iatoki.judgels.gabriel.blackbox;

import java.io.File;

public interface Scorer {

    ScoringResult score(File testCaseInput, File testCaseOutput, File evaluationOutputFile) throws ScoringException;
}
