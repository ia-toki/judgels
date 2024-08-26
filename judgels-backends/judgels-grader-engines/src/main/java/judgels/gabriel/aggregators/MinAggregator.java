package judgels.gabriel.aggregators;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.AggregationResult;
import judgels.gabriel.api.Aggregator;
import judgels.gabriel.api.SubtaskVerdict;
import judgels.gabriel.api.TestCaseVerdict;
import judgels.gabriel.api.Verdict;

public final class MinAggregator implements Aggregator {
    @Override
    public AggregationResult aggregate(List<TestCaseVerdict> testCaseVerdicts, double subtaskPoints) {
        Verdict aggregatedVerdict = Verdict.ACCEPTED;
        double aggregatedPoints = subtaskPoints;
        ImmutableList.Builder<String> testCasePoints = ImmutableList.builder();

        for (TestCaseVerdict testCaseVerdict : testCaseVerdicts) {
            Verdict verdict = testCaseVerdict.getVerdict();
            if (verdict.ordinal() > aggregatedVerdict.ordinal()) {
                aggregatedVerdict = verdict;
            }

            String points = "";
            if (verdict == Verdict.OK) {
                double okPoints = 0.0;
                if (testCaseVerdict.getPercentage().isPresent()) {
                    okPoints = testCaseVerdict.getPercentage().get() * subtaskPoints / 100.0;
                    points = PointUtils.formatPoints(testCaseVerdict.getPercentage().get()) + "%";
                } else if (testCaseVerdict.getPoints().isPresent()) {
                    okPoints = testCaseVerdict.getPoints().get();
                    points = PointUtils.formatPoints(okPoints);
                }
                aggregatedPoints = Math.min(aggregatedPoints, okPoints);
            } else if (verdict == Verdict.ACCEPTED) {
                points = "*";
            } else if (verdict == Verdict.SKIPPED) {
                aggregatedPoints = 0.0;
                points = "?";
            } else {
                aggregatedPoints = 0.0;
                points = "X";
            }

            testCasePoints.add(points);
        }

        return new AggregationResult.Builder()
                .subtaskVerdict(SubtaskVerdict.of(aggregatedVerdict, aggregatedPoints))
                .testCasePoints(testCasePoints.build())
                .build();
    }
}
