package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGradingResultDetails.class)
public interface GradingResultDetails {
    Map<String, String> getCompilationOutputs();
    List<TestGroupResult> getTestDataResults();
    List<SubtaskResult> getSubtaskResults();
}
