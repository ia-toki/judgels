package judgels.jophiel.auth.google;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGoogleAuthConfiguration.class)
public interface GoogleAuthConfiguration {
    String getClientId();

    class Builder extends ImmutableGoogleAuthConfiguration.Builder {}
}
