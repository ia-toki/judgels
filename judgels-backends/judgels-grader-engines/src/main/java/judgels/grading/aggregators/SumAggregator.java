package judgels.grading.aggregators;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.grading.api.AggregationResult;
import judgels.grading.api.Aggregator;
import judgels.grading.api.SubtaskVerdict;
import judgels.grading.api.TestCaseVerdict;
import judgels.grading.api.Verdict;

public final class SumAggregator implements Aggregator {
    @Override
    public AggregationResult aggregate(List<TestCaseVerdict> testCaseVerdicts, double subtaskPoints) {
        if (testCaseVerdicts.isEmpty()) {
            return new AggregationResult.Builder()
                    .subtaskVerdict(SubtaskVerdict.of(Verdict.ACCEPTED, subtaskPoints))
                    .build();
        }

        double testCaseFullPoints = subtaskPoints / testCaseVerdicts.size();

        Verdict aggregatedVerdict = Verdict.ACCEPTED;
        double aggregatedPoints = 0;
        ImmutableList.Builder<String> testCasePoints = ImmutableList.builder();

        for (TestCaseVerdict testCaseVerdict : testCaseVerdicts) {
            Verdict verdict = testCaseVerdict.getVerdict();
            if (verdict.ordinal() > aggregatedVerdict.ordinal()) {
                aggregatedVerdict = verdict;
            }

            double points = 0;
            if (verdict == Verdict.ACCEPTED) {
                points = testCaseFullPoints;
            } else if (verdict == Verdict.OK) {
                if (testCaseVerdict.getPoints().isPresent()) {
                    points = testCaseVerdict.getPoints().get();
                } else if (testCaseVerdict.getPercentage().isPresent()) {
                    points = testCaseVerdict.getPercentage().get() * testCaseFullPoints / 100.0;
                }
            }
            aggregatedPoints += points;

            testCasePoints.add(PointUtils.formatPoints(points));
        }
        if (aggregatedVerdict == Verdict.SKIPPED) {
            aggregatedVerdict = Verdict.OK;
        }

        return new AggregationResult.Builder()
                .subtaskVerdict(SubtaskVerdict.of(aggregatedVerdict, aggregatedPoints))
                .testCasePoints(testCasePoints.build())
                .build();
    }
}
