package judgels.sandalphon.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.gabriel.api.SubtaskResult;
import judgels.gabriel.api.TestGroupResult;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableRawGradingResultDetails.class)
public interface RawGradingResultDetails {
    Map<String, String> getCompilationOutputs();
    List<TestGroupResult> getTestDataResults();
    List<SubtaskResult> getSubtaskResults();
}
