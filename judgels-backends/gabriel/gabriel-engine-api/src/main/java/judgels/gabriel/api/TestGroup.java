package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTestGroup.class)
public interface TestGroup {
    int getId();
    List<TestCase> getTestCases();

    static TestGroup of(int id, List<TestCase> testCases) {
        return new TestGroup.Builder().id(id).testCases(testCases).build();
    }

    class Builder extends ImmutableTestGroup.Builder {}
}
