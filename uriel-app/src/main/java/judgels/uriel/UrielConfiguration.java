package judgels.uriel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.uriel.jophiel.JophielConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUrielConfiguration.class)
public interface UrielConfiguration {
    @JsonProperty("jophiel")
    JophielConfiguration getJophielConfig();

    class Builder extends ImmutableUrielConfiguration.Builder {}
}
