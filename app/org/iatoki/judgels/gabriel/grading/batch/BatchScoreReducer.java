package org.iatoki.judgels.gabriel.grading.batch;

import org.iatoki.judgels.gabriel.blackbox.ScoringStatus;
import org.iatoki.judgels.gabriel.blackbox.ScoringVerdict;
import org.iatoki.judgels.gabriel.blackbox.ScoreReducer;

import java.util.List;

public final class BatchScoreReducer implements ScoreReducer {

    @Override
    public double reduce(List<ScoringVerdict> verdicts, double subtaskScore, String subtaskParam) {
        for (ScoringVerdict verdict : verdicts) {
            if (verdict.getStatus() != ScoringStatus.OK) {
                return 0.0;
            }
        }

        return subtaskScore;
    }
}
