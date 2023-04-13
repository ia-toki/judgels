package judgels.jophiel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJophielClientConfiguration.class)
public interface JophielClientConfiguration {
    JophielClientConfiguration DEFAULT = new Builder()
            .baseUrl("http://localhost:9001")
            .build();

    String getBaseUrl();

    class Builder extends ImmutableJophielClientConfiguration.Builder {}
}
