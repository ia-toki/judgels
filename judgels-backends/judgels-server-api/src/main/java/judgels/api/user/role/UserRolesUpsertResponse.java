package judgels.api.user.role;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRolesUpsertResponse.class)
public interface UserRolesUpsertResponse {
    Map<String, Profile> getUpsertedUserProfilesMap();

    class Builder extends ImmutableUserRolesUpsertResponse.Builder {}
}
