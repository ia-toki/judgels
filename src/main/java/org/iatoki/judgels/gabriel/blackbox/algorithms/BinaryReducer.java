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

public final class BinaryReducer implements Reducer {
    @Override
    public SubtaskResult reduceTestCases(List<TestCaseResult> testCaseResults, Subtask subtask) throws ReductionException {
        double score = subtask.getPoints();
        List<EvaluationVerdict> evaluationVerdicts = Lists.newArrayList();
        List<ScoringVerdict> scoringVerdicts = Lists.newArrayList();
        for (TestCaseResult result : testCaseResults) {
            NormalVerdict verdict = result.getVerdict();
            if (verdict != ScoringVerdict.ACCEPTED) {
                score = 0.0;
            }
            if (verdict instanceof EvaluationVerdict) {
                evaluationVerdicts.add((EvaluationVerdict) verdict);
            } else {
                scoringVerdicts.add((ScoringVerdict) verdict);
            }
        }

        Collections.sort(evaluationVerdicts);
        Collections.sort(scoringVerdicts);

        if (!evaluationVerdicts.isEmpty()) {
            return new SubtaskResult(evaluationVerdicts.get(evaluationVerdicts.size() - 1), score);
        } else if (!scoringVerdicts.isEmpty()) {
            return new SubtaskResult(scoringVerdicts.get(scoringVerdicts.size() - 1), score);
        } else {
            return new SubtaskResult(ScoringVerdict.ACCEPTED, 0.0);
        }
    }

    @Override
    public OverallResult reduceSubtasks(List<SubtaskResult> subtaskResults) {
        return new OverallResult(subtaskResults.get(0).getVerdict(), (int) subtaskResults.get(0).getScore());
    }
}
