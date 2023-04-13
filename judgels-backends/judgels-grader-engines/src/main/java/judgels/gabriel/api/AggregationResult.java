package judgels.gabriel.api;

import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
public interface AggregationResult {
    SubtaskVerdict getSubtaskVerdict();
    List<String> getTestCasePoints();

    class Builder extends ImmutableAggregationResult.Builder {}
}
