package judgels.uriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUrielClientConfiguration.class)
public interface UrielClientConfiguration {
    UrielClientConfiguration DEFAULT = new Builder()
            .baseUrl("http://localhost:9004")
            .build();

    String getBaseUrl();

    class Builder extends ImmutableUrielClientConfiguration.Builder {}
}
