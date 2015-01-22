package org.iatoki.judgels.gabriel.blackbox;

import java.io.File;
import java.util.Map;

public interface Scorer {
    ScoringResult score(Map<String, File> evaluationOutputFiles, File testCaseInput, File testCaseOutput) throws ScoringException;
}
