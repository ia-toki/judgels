package judgels.gabriel.api;

import java.io.File;

public interface Scorer {
    ScoringResult score(File input, File output, File evaluationOutput) throws ScoringException;
}
