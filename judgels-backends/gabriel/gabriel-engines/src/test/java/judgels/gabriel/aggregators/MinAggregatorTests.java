package judgels.gabriel.aggregators;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.SubtaskVerdict;
import judgels.gabriel.api.TestCaseAggregationResult;
import judgels.gabriel.api.TestCaseVerdict;
import judgels.gabriel.api.Verdict;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MinAggregatorTests {
    private MinAggregator aggregator;

    @BeforeEach
    void before() {
        aggregator = new MinAggregator();
    }

    @Test
    void aggregate_full_points() {
        List<TestCaseVerdict> testCaseVerdicts = ImmutableList.of(
                new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).build());

        TestCaseAggregationResult result = aggregator.aggregate(testCaseVerdicts, 70.0);
        assertThat(result.getSubtaskVerdict()).isEqualTo(SubtaskVerdict.of(Verdict.ACCEPTED, 70.0));
        assertThat(result.getTestCasePoints()).containsExactly("*", "*");
    }

    @Test
    void aggregate_zero_points() {
        List<TestCaseVerdict> testCaseVerdicts = ImmutableList.of(
                new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.RUNTIME_ERROR).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.WRONG_ANSWER).build());

        TestCaseAggregationResult result = aggregator.aggregate(testCaseVerdicts, 70.0);
        assertThat(result.getSubtaskVerdict()).isEqualTo(SubtaskVerdict.of(Verdict.RUNTIME_ERROR, 0.0));
        assertThat(result.getTestCasePoints()).containsExactly("*", "X", "X");
    }

    @Test
    void aggregate_zero_points_with_skipped() {
        List<TestCaseVerdict> testCaseVerdicts = ImmutableList.of(
                new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.RUNTIME_ERROR).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.SKIPPED).build());

        TestCaseAggregationResult result = aggregator.aggregate(testCaseVerdicts, 70.0);
        assertThat(result.getSubtaskVerdict()).isEqualTo(SubtaskVerdict.of(Verdict.RUNTIME_ERROR, 0.0));
        assertThat(result.getTestCasePoints()).containsExactly("*", "X", "?");
    }

    @Test
    void aggregate_zero_points_with_accepted_and_skipped() {
        List<TestCaseVerdict> testCaseVerdicts = ImmutableList.of(
                new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.SKIPPED).build());

        TestCaseAggregationResult result = aggregator.aggregate(testCaseVerdicts, 70.0);
        assertThat(result.getSubtaskVerdict()).isEqualTo(SubtaskVerdict.of(Verdict.OK, 0.0));
        assertThat(result.getTestCasePoints()).containsExactly("*", "*", "?");
    }

    @Test
    void aggregate_min_ok_points() {
        List<TestCaseVerdict> testCaseVerdicts = ImmutableList.of(
                new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.OK).points(20.0).build(),
                new TestCaseVerdict.Builder().verdict(Verdict.OK).points(30.0).build());

        TestCaseAggregationResult result = aggregator.aggregate(testCaseVerdicts, 70.0);
        assertThat(result.getSubtaskVerdict()).isEqualTo(SubtaskVerdict.of(Verdict.OK, 20.0));
        assertThat(result.getTestCasePoints()).containsExactly("*", "20.0", "30.0");
    }

    @Test
    void aggregate_empty() {
        List<TestCaseVerdict> testCaseVerdicts = ImmutableList.of();

        TestCaseAggregationResult result = aggregator.aggregate(testCaseVerdicts, 70.0);
        assertThat(result.getSubtaskVerdict()).isEqualTo(SubtaskVerdict.of(Verdict.ACCEPTED, 70.0));
        assertThat(result.getTestCasePoints()).isEmpty();
    }
}
