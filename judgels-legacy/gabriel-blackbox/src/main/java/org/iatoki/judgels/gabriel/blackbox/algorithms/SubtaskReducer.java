package org.iatoki.judgels.gabriel.blackbox.algorithms;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.iatoki.judgels.gabriel.blackbox.EvaluationVerdict;
import org.iatoki.judgels.gabriel.blackbox.NormalVerdict;
import org.iatoki.judgels.gabriel.blackbox.ReductionException;
import org.iatoki.judgels.gabriel.blackbox.ScoringVerdict;
import org.iatoki.judgels.gabriel.blackbox.Subtask;
import org.iatoki.judgels.gabriel.blackbox.SubtaskResult;
import org.iatoki.judgels.gabriel.blackbox.TestCaseResult;

import java.util.List;

public final class SubtaskReducer extends AbstractReducer {

    @Override
    public List<TestCaseResult> improveTestCaseResults(List<TestCaseResult> testCaseResults, Subtask subtask) {
        if (testCaseResults.isEmpty()) {
            return testCaseResults;
        }

        ImmutableList.Builder<TestCaseResult> results = ImmutableList.builder();

        for (TestCaseResult result : testCaseResults) {
            NormalVerdict verdict = result.getVerdict();
            String score = result.getScore();

            if (verdict == ScoringVerdict.ACCEPTED) {
                results.add(new TestCaseResult(verdict, improveScore("*", score)));
            } else if (verdict == EvaluationVerdict.SKIPPED) {
                results.add(new TestCaseResult(verdict, improveScore("?", score)));
            } else {
                results.add(new TestCaseResult(verdict, improveScore("X", score)));
            }
        }

        return results.build();
    }

    @Override
    public SubtaskResult reduceTestCaseResults(List<TestCaseResult> testCaseResults, Subtask subtask) throws ReductionException {
        if (testCaseResults.isEmpty()) {
            return new SubtaskResult(ScoringVerdict.OK, 0.0);
        }

        double score = subtask.getPoints();
        for (TestCaseResult result : testCaseResults) {
            NormalVerdict verdict = result.getVerdict();
            if (verdict == ScoringVerdict.OK) {
                score = Math.min(score, getOkScore(result.getScore()));
            } else if (verdict != ScoringVerdict.ACCEPTED) {
                score = 0.0;
            }
        }
        return new SubtaskResult(getWorstVerdict(Lists.transform(testCaseResults, r -> r.getVerdict())), score);
    }
}
