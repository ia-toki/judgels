package judgels.uriel.api.admin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAdminDeleteResponse.class)
public interface AdminDeleteResponse {
    Map<String, Profile> getDeletedAdminProfilesMap();

    class Builder extends ImmutableAdminDeleteResponse.Builder {}
}
