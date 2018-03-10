package judgels.jophiel.api.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePublicUser.class)
public interface PublicUser {
    String getJid();
    String getUsername();

    class Builder extends ImmutablePublicUser.Builder {}
}
