package judgels.jerahmeel.api.admin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAdminsUpsertResponse.class)
public interface AdminsUpsertResponse {
    Map<String, Profile> getInsertedAdminProfilesMap();
    Map<String, Profile> getAlreadyAdminProfilesMap();

    class Builder extends ImmutableAdminsUpsertResponse.Builder {}
}
