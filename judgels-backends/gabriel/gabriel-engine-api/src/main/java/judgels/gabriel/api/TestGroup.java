package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTestGroup.class)
public interface TestGroup {
    int getId();
    List<TestCase> getTestCases();

    class Builder extends ImmutableTestGroup.Builder {}
}
