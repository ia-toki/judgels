package judgels.gabriel.api;

import java.io.File;

public interface Evaluator {
    EvaluationResult evaluate(File input, File output) throws EvaluationException;

    default GenerationResult generate(File input, File output) throws EvaluationException {
        throw new UnsupportedOperationException();
    }

    default ScoringResult score(File input, File output) throws ScoringException {
        throw new UnsupportedOperationException();
    }
}
