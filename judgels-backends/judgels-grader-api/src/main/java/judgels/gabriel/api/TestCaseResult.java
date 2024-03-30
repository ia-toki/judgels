package judgels.gabriel.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTestCaseResult.class)
public interface TestCaseResult {
    Verdict getVerdict();
    String getScore();
    Optional<SandboxExecutionResult> getExecutionResult();
    List<Integer> getSubtaskIds();

    @JsonInclude(Include.NON_ABSENT)
    Optional<String> getRevealedInput();

    @JsonInclude(Include.NON_ABSENT)
    Optional<String> getRevealedSolutionOutput();

    class Builder extends ImmutableTestCaseResult.Builder {}
}
