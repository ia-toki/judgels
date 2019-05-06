package judgels.gabriel.api;

import java.util.List;

public interface TestCaseAggregator {
    TestCaseAggregationResult aggregate(List<TestCaseVerdict> testCaseVerdicts, double subtaskPoints);
}
