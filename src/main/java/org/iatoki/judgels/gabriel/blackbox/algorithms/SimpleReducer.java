package org.iatoki.judgels.gabriel.blackbox.algorithms;

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
    public SubtaskResult reduceTestCases(List<TestCaseResult> testCaseResults, Subtask subtask) throws ReductionException {
        if (testCaseResults.isEmpty()) {
            return new SubtaskResult(ScoringVerdict.OK, 0.0);
        }

        double testCasesCount = testCaseResults.size();

        double score = 0;
        for (TestCaseResult result : testCaseResults) {
            NormalVerdict verdict = result.getVerdict();

            if (verdict == ScoringVerdict.ACCEPTED) {
                score += 100.0 / testCasesCount;
            }
            else if (verdict == ScoringVerdict.OK) {
                score += getOkScore(result.getScore());
            }
        }
        return new SubtaskResult(getWorstVerdict(Lists.transform(testCaseResults, r -> r.getVerdict())), score);
    }

    private double getOkScore(String score) throws ReductionException {
        String[] tokens = score.split(" ", 1);
        if (tokens.length == 0) {
            throw new ReductionException("Invalid score for OK: " + score);
        }

        try {
            return Double.parseDouble(tokens[0]);
        } catch (NumberFormatException e) {
            throw new ReductionException("Invalid score for OK: " + score + "(must contain a number in the beginning of the second line)");
        }
    }
}
