package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTestGroupResult.class)
public interface TestGroupResult {
    int getId();
    List<TestCaseResult> getTestCaseResults();
}
