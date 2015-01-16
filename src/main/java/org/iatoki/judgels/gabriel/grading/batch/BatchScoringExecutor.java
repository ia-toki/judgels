package org.iatoki.judgels.gabriel.grading.batch;

import org.iatoki.judgels.gabriel.Language;
import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.GradingContext;
import org.iatoki.judgels.gabriel.blackbox.ScoringExecutor;
import org.iatoki.judgels.gabriel.blackbox.ScoringStatus;
import org.iatoki.judgels.gabriel.blackbox.ScoringVerdict;

import java.io.File;

public final class BatchScoringExecutor implements ScoringExecutor {

    @Override
    public ScoringVerdict score(Sandbox sandbox, GradingContext context, File testCaseInput, File testCaseOutput) {
        return new ScoringVerdict(null, ScoringStatus.OK, "100");
    }
}
