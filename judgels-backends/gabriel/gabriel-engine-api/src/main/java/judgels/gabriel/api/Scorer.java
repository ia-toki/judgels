package judgels.gabriel.api;

import java.io.File;

public interface Scorer {
    ScoringResult score(File testCaseInput, File testCaseOutput, File evaluationOutputFile) throws ScoringException;
}
