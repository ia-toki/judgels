package judgels.jerahmeel.api.admin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAdminsDeleteResponse.class)
public interface AdminsDeleteResponse {
    Map<String, Profile> getDeletedAdminProfilesMap();

    class Builder extends ImmutableAdminsDeleteResponse.Builder {}
}
