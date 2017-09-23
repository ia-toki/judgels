package judgels.sealtiel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.dropwizard.Configuration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSealtielApplicationConfiguration.class)
public abstract class SealtielApplicationConfiguration extends Configuration {
    @JsonProperty("sealtiel")
    public abstract SealtielConfiguration getSealtielConfig();

    @Override
    public abstract String toString();
}
