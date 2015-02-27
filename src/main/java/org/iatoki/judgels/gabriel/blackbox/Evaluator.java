package org.iatoki.judgels.gabriel.blackbox;

import java.io.File;

public interface Evaluator {
    EvaluationResult evaluate(File testCaseInput) throws EvaluationException;
}
