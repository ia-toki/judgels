package org.iatoki.judgels.gabriel.blackbox;

import java.util.Set;

public interface ScoreReducer {
    double reduce(Set<ScoringVerdict> verdicts, double subtaskPoints, String subtaskParam);
}
