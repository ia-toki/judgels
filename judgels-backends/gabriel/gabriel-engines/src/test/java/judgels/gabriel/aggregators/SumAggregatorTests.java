package judgels.gabriel.aggregators;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.AggregationResult;
import judgels.gabriel.api.SubtaskVerdict;
import judgels.gabriel.api.TestCaseVerdict;
import judgels.gabriel.api.Verdict;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SumAggregatorTests {
    private SumAggregator aggregator;

    @BeforeEach
    void before() {
        aggregator = new SumAggregator();
    }

    @Test
    void aggregate_full_points() {
        List<TestCaseVerdict> testCaseVerdicts = ImmutableList.of(
                new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).build());

        AggregationResult result = aggregator.aggregate(testCaseVerdicts, 100.0);
        assertThat(result.getSubtaskVerdict()).isEqualTo(SubtaskVerdict.of(Verdict.ACCEPTED, 100.0));
        assertThat(result.getTestCasePoints()).containsExactly("50.0", "50.0");
    }

    @Test
    void aggregate_partial_points() {
        List<TestCaseVerdict> testCaseVerdicts = ImmutableList.of(
                new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.TIME_LIMIT_EXCEEDED).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.OK).points(30.0).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.WRONG_ANSWER).build());

        AggregationResult result = aggregator.aggregate(testCaseVerdicts, 100.0);
        assertThat(result.getSubtaskVerdict()).isEqualTo(SubtaskVerdict.of(Verdict.TIME_LIMIT_EXCEEDED, 55.0));
        assertThat(result.getTestCasePoints()).containsExactly("25.0", "0.0", "30.0", "0.0");
    }

    @Test
    void aggregate_partial_points_with_skipped() {
        List<TestCaseVerdict> testCaseVerdicts = ImmutableList.of(
                new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.SKIPPED).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.TIME_LIMIT_EXCEEDED).build());

        AggregationResult result = aggregator.aggregate(testCaseVerdicts, 100.0);
        assertThat(result.getSubtaskVerdict()).isEqualTo(SubtaskVerdict.of(Verdict.TIME_LIMIT_EXCEEDED, 50.0));
        assertThat(result.getTestCasePoints()).containsExactly("25.0", "25.0", "0.0", "0.0");
    }

    @Test
    void aggregate_partial_points_with_accepted_and_skipped() {
        List<TestCaseVerdict> testCaseVerdicts = ImmutableList.of(
                new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.SKIPPED).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.SKIPPED).build());

        AggregationResult result = aggregator.aggregate(testCaseVerdicts, 100.0);
        assertThat(result.getSubtaskVerdict()).isEqualTo(SubtaskVerdict.of(Verdict.OK, 50.0));
        assertThat(result.getTestCasePoints()).containsExactly("25.0", "25.0", "0.0", "0.0");
    }

    @Test
    void aggregate_empty() {
        List<TestCaseVerdict> testCaseVerdicts = ImmutableList.of();

        AggregationResult result = aggregator.aggregate(testCaseVerdicts, 100.0);
        assertThat(result.getSubtaskVerdict()).isEqualTo(SubtaskVerdict.of(Verdict.ACCEPTED, 100.0));
        assertThat(result.getTestCasePoints()).isEmpty();
    }
}
