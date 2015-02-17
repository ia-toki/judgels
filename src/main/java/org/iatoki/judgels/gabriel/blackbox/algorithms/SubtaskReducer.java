package org.iatoki.judgels.gabriel.blackbox.algorithms;

import com.google.common.collect.Lists;
import org.iatoki.judgels.gabriel.blackbox.EvaluationVerdict;
import org.iatoki.judgels.gabriel.blackbox.NormalVerdict;
import org.iatoki.judgels.gabriel.blackbox.OverallResult;
import org.iatoki.judgels.gabriel.blackbox.Reducer;
import org.iatoki.judgels.gabriel.blackbox.ReductionException;
import org.iatoki.judgels.gabriel.blackbox.ScoringVerdict;
import org.iatoki.judgels.gabriel.blackbox.Subtask;
import org.iatoki.judgels.gabriel.blackbox.SubtaskResult;
import org.iatoki.judgels.gabriel.blackbox.TestCaseResult;

import java.util.Collections;
import java.util.List;

public final class SubtaskReducer implements Reducer {

    @Override
    public SubtaskResult reduceTestCases(List<TestCaseResult> testCaseResults, Subtask subtask) throws ReductionException {
        if (testCaseResults.isEmpty()) {
            return new SubtaskResult(ScoringVerdict.OK, 0.0);
        } else {
            double score = subtask.getPoints();
            for (TestCaseResult result : testCaseResults) {
                NormalVerdict verdict = result.getVerdict();
                if (verdict != ScoringVerdict.ACCEPTED) {
                    score = 0.0;
                }
            }
            return new SubtaskResult(getWorstVerdict(Lists.transform(testCaseResults, r -> r.getVerdict())), score);
        }
    }

    @Override
    public OverallResult reduceSubtasks(List<SubtaskResult> subtaskResults) {
        if (subtaskResults.size() == 1) {
            return new OverallResult(subtaskResults.get(0).getVerdict(), null, (int) Math.round(subtaskResults.get(0).getScore()));
        } else {
            double score = 0;
            for (SubtaskResult result : subtaskResults) {
                score += result.getScore();
            }

            NormalVerdict worstVerdict = getWorstVerdict(Lists.transform(subtaskResults, r -> r.getVerdict()));
            if (worstVerdict == ScoringVerdict.ACCEPTED) {
                return new OverallResult(ScoringVerdict.ACCEPTED, null, (int) Math.round(score));
            } else {
                return new OverallResult(ScoringVerdict.OK, "worst: " + worstVerdict.getCode(), (int) Math.round(score));
            }
        }
    }

    private NormalVerdict getWorstVerdict(List<NormalVerdict> verdicts) {
        List<EvaluationVerdict> evaluationVerdicts = Lists.newArrayList();
        List<ScoringVerdict> scoringVerdicts = Lists.newArrayList();

        for (NormalVerdict verdict : verdicts) {
            if (verdict instanceof EvaluationVerdict) {
                evaluationVerdicts.add((EvaluationVerdict) verdict);
            } else {
                scoringVerdicts.add((ScoringVerdict) verdict);
            }
        }

        Collections.sort(evaluationVerdicts);
        Collections.sort(scoringVerdicts);

        if (!evaluationVerdicts.isEmpty()) {
            return evaluationVerdicts.get(evaluationVerdicts.size() - 1);
        } else if (!scoringVerdicts.isEmpty()) {
            return scoringVerdicts.get(scoringVerdicts.size() - 1);
        } else {
            return ScoringVerdict.OK;
        }
    }
}
