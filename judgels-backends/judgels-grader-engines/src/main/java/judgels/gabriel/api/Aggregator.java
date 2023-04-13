package judgels.gabriel.api;

import java.util.List;

public interface Aggregator {
    AggregationResult aggregate(List<TestCaseVerdict> testCaseVerdicts, double subtaskPoints);
}
