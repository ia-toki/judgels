package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.Language;

import java.io.File;

public interface EvaluationExecutor {
    EvaluationVerdict evaluate(Sandbox sandbox, GradingContext context, File testCaseInput);
}
