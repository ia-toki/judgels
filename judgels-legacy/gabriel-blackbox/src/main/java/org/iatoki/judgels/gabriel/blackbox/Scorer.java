package org.iatoki.judgels.gabriel.blackbox;

import judgels.gabriel.api.ScoringResult;

import java.io.File;

public interface Scorer {

    ScoringResult score(File testCaseInput, File testCaseOutput, File evaluationOutputFile) throws judgels.gabriel.api.ScoringException;
}
