package judgels.jophiel.api.user.profile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePublicUserProfile.class)
public interface PublicUserProfile {
    String getUserJid();
    String getUsername();
    Optional<String> getNationality();
    Optional<String> getName();

    class Builder extends ImmutablePublicUserProfile.Builder {}
}
