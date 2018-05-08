package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTestCaseResult.class)
public interface TestCaseResult {
    Verdict getVerdict();
    String getScore();
    SandboxExecutionResult getExecutionResult();
    List<Integer> getSubtaskIds();
}
