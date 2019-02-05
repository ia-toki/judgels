package judgels.jerahmeel.api.admin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAdminsResponse.class)
public interface AdminsResponse {
    Page<Admin> getData();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableAdminsResponse.Builder {}
}
