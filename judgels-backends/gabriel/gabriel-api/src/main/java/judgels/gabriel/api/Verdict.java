package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableVerdict.class)
public interface Verdict {
    String getCode();
    String getName();

    class Builder extends ImmutableVerdict.Builder {}
}
