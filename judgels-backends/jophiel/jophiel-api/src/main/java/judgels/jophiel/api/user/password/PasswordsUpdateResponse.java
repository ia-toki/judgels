package judgels.jophiel.api.user.password;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePasswordsUpdateResponse.class)
public interface PasswordsUpdateResponse {
    Map<String, Profile> getUpdatedUserProfilesMap();

    class Builder extends ImmutablePasswordsUpdateResponse.Builder {}
}
