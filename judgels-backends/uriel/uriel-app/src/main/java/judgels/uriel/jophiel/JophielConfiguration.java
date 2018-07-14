package judgels.uriel.jophiel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJophielConfiguration.class)
public interface JophielConfiguration {
    JophielConfiguration DEFAULT = new Builder()
            .baseUrl("http://localhost:9001")
            .build();

    String getBaseUrl();

    class Builder extends ImmutableJophielConfiguration.Builder {}
}
