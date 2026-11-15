package judgels.api.user.role;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRolesAddResponse.class)
public interface UserRolesAddResponse {
    Map<String, Profile> getAddedUserProfilesMap();

    class Builder extends ImmutableUserRolesAddResponse.Builder {}
}
