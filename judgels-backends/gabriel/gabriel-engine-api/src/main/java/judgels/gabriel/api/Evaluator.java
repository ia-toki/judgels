package judgels.gabriel.api;

import java.io.File;

public interface Evaluator {
    EvaluationResult evaluate(File testCaseInput) throws EvaluationException;
    String getEvaluationResultFilename(File testcaseInputFile);
}
