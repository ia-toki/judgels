package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTestCase.class)
public interface TestCase {
    String getInput();
    String getOutput();
    Set<String> getSubtaskIds();

    class Builder extends ImmutableTestCase.Builder {}
}
