package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTestCase.class)
public interface TestCase {
    String getInput();
    String getOutput();
    Set<Integer> getSubtaskIds();

    static TestCase of(String input, String output, Set<Integer> subtaskIds) {
        return new TestCase.Builder()
                .input(input)
                .output(output)
                .subtaskIds(subtaskIds)
                .build();
    }

    class Builder extends ImmutableTestCase.Builder {}
}
