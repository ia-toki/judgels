package org.iatoki.judgels.gabriel.blackbox;

import java.io.File;
import java.util.Map;

public interface Evaluator {
    EvaluationResult evaluate(Map<String, File> executableFiles, File testCaseInput) throws EvaluationException;
}
