package judgels.gabriel.aggregators;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.AggregationResult;
import judgels.gabriel.api.Aggregator;
import judgels.gabriel.api.SubtaskVerdict;
import judgels.gabriel.api.TestCaseVerdict;
import judgels.gabriel.api.Verdict;

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
                points = testCaseVerdict.getPoints().orElse(0.0);
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
