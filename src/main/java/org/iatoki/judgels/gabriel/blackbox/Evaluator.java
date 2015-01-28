package org.iatoki.judgels.gabriel.blackbox;

import java.io.File;
import java.util.Set;

public interface Evaluator {
    EvaluationResult evaluate(File testCaseInput, Set<Integer> subtaskNumbers) throws EvaluationException;
}
