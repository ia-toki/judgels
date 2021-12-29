package judgels.jophiel.auth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.jophiel.auth.google.GoogleAuthConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAuthConfiguration.class)
public interface AuthConfiguration {
    Optional<GoogleAuthConfiguration> getGoogle();

    class Builder extends ImmutableAuthConfiguration.Builder {}
}
