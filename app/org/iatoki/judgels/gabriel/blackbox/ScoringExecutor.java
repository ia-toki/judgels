package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.Sandbox;

import java.io.File;

public interface ScoringExecutor {
    ScoringVerdict score(Sandbox sandbox, GradingContext context, File testCaseInput, File testCaseOutput);
}
