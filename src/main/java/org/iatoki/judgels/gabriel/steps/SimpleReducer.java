package org.iatoki.judgels.gabriel.steps;

import org.iatoki.judgels.gabriel.Verdict;
import org.iatoki.judgels.gabriel.blackbox.Reducer;
import org.iatoki.judgels.gabriel.blackbox.ReductionException;

import java.util.List;
import java.util.Set;

public final class SimpleReducer implements Reducer {

    @Override
    public double reduceTestCaseScores(List<String> scores, int subtaskPoints, String subtaskParam) throws ReductionException {
        for (String score : scores) {
            if (!score.equals("100")) {
                return 0.0;
            }
        }
        return subtaskPoints;
    }

    @Override
    public int reduceSubtaskScores(List<Double> scores) throws ReductionException {
        int res = 0;
        for (double score : scores) {
            res += (int) score;
        }
        return res;
    }

    @Override
    public Verdict reduceVerdicts(Set<Verdict> verdicts) throws ReductionException {
        return Verdict.OK;
    }
}
