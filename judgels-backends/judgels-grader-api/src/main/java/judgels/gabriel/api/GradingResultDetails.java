package judgels.gabriel.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGradingResultDetails.class)
public interface GradingResultDetails {
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    Optional<String> getErrorMessage();

    Map<String, String> getCompilationOutputs();
    List<TestGroupResult> getTestDataResults();
    List<SubtaskResult> getSubtaskResults();

    class Builder extends ImmutableGradingResultDetails.Builder {}
}
