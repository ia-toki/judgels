package judgels.uriel.api.admin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAdminUpsertResponse.class)
public interface AdminUpsertResponse {
    Map<String, Profile> getInsertedAdminProfilesMap();
    Map<String, Profile> getAlreadyAdminProfilesMap();

    class Builder extends ImmutableAdminUpsertResponse.Builder {}
}
