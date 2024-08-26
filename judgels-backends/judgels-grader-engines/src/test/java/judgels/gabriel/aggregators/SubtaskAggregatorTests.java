package judgels.gabriel.aggregators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import judgels.gabriel.api.SubtaskVerdict;
import judgels.gabriel.api.Verdict;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubtaskAggregatorTests {
    private SubtaskAggregator aggregator;

    @BeforeEach
    void before() {
        aggregator = new SubtaskAggregator();
    }

    @Test
    void aggregate() {
        List<SubtaskVerdict> subtaskVerdicts = List.of(
                new SubtaskVerdict.Builder().verdict(Verdict.ACCEPTED).points(10).build(),
                new SubtaskVerdict.Builder().verdict(Verdict.TIME_LIMIT_EXCEEDED).points(20).build(),
                new SubtaskVerdict.Builder().verdict(Verdict.OK).points(30).build(),
                new SubtaskVerdict.Builder().verdict(Verdict.SKIPPED).points(0).build(),
                new SubtaskVerdict.Builder().verdict(Verdict.WRONG_ANSWER).points(0).build());

        assertThat(aggregator.aggregate(subtaskVerdicts)).isEqualTo(SubtaskVerdict.of(Verdict.TIME_LIMIT_EXCEEDED, 60));
    }

    @Test
    void aggregate_skipped() {
        List<SubtaskVerdict> subtaskVerdicts = List.of(
                new SubtaskVerdict.Builder().verdict(Verdict.SKIPPED).points(0).build(),
                new SubtaskVerdict.Builder().verdict(Verdict.SKIPPED).points(0).build());

        assertThat(aggregator.aggregate(subtaskVerdicts)).isEqualTo(SubtaskVerdict.of(Verdict.OK, 0));
    }
}
