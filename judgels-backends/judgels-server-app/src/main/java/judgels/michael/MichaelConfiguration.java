package judgels.michael;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableMichaelConfiguration.class)
public interface MichaelConfiguration {
    String getName();

    class Builder extends ImmutableMichaelConfiguration.Builder {}
}
