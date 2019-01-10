package org.iatoki.judgels.gabriel.blackbox.algorithms;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.iatoki.judgels.gabriel.blackbox.NormalVerdict;
import org.iatoki.judgels.gabriel.blackbox.ReductionException;
import org.iatoki.judgels.gabriel.blackbox.ScoringVerdict;
import org.iatoki.judgels.gabriel.blackbox.Subtask;
import org.iatoki.judgels.gabriel.blackbox.SubtaskResult;
import org.iatoki.judgels.gabriel.blackbox.TestCaseResult;

import java.util.List;

public final class SimpleReducer extends AbstractReducer {

    @Override
    public List<TestCaseResult> improveTestCaseResults(List<TestCaseResult> testCaseResults, Subtask subtask) {
        if (testCaseResults.isEmpty()) {
            return testCaseResults;
        }

        ImmutableList.Builder<TestCaseResult> results = ImmutableList.builder();

        double testCaseFullScore = 100.0 / testCaseResults.size();

        for (TestCaseResult result : testCaseResults) {
            NormalVerdict verdict = result.getVerdict();
            String score = result.getScore();

            if (verdict == ScoringVerdict.ACCEPTED) {
                results.add(new TestCaseResult(verdict, improveScore(testCaseFullScore, score)));
            } else if (verdict == ScoringVerdict.OK) {
                results.add(result);
            } else {
                results.add(new TestCaseResult(verdict, improveScore(0, score)));
            }
        }

        return results.build();
    }

    @Override
    public SubtaskResult reduceTestCaseResults(List<TestCaseResult> testCaseResults, Subtask subtask) throws ReductionException {
        if (testCaseResults.isEmpty()) {
            return new SubtaskResult(ScoringVerdict.OK, 0.0);
        }

        double testCaseFullScore = 100.0 / testCaseResults.size();

        double score = 0;
        for (TestCaseResult result : testCaseResults) {
            NormalVerdict verdict = result.getVerdict();

            if (verdict == ScoringVerdict.ACCEPTED) {
                score += testCaseFullScore;
            } else if (verdict == ScoringVerdict.OK) {
                score += getOkScore(result.getScore());
            }
        }
        return new SubtaskResult(getWorstVerdict(Lists.transform(testCaseResults, r -> r.getVerdict())), score);
    }

    private String improveScore(double score, String originalScore) {
        if (originalScore.isEmpty()) {
            return "" + score;
        } else {
            return "" + score + " (" + originalScore + ")";
        }
    }
}
