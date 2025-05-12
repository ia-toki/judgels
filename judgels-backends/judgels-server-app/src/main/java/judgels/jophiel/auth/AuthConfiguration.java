package judgels.jophiel.auth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;
import tlx.jophiel.auth.google.GoogleAuthConfiguration;

@Value.Immutable
@JsonDeserialize(as = ImmutableAuthConfiguration.class)
public interface AuthConfiguration {
    Optional<GoogleAuthConfiguration> getGoogle();

    class Builder extends ImmutableAuthConfiguration.Builder {}
}
