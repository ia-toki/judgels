package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.Verdict;

import java.util.List;
import java.util.Set;

public interface Reducer {
    double reduceTestCaseScores(List<String> scores, int subtaskPoints, String subtaskParam) throws ReductionException;

    int reduceSubtaskScores(List<Double> scores) throws ReductionException;

    Verdict reduceVerdicts(Set<Verdict> verdicts) throws ReductionException;
}
