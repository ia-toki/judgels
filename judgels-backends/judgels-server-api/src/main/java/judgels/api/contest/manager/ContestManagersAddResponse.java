package judgels.api.contest.manager;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestManagersAddResponse.class)
public interface ContestManagersAddResponse {
    Map<String, Profile> getAddedManagerProfilesMap();
    Map<String, Profile> getAlreadyManagerProfilesMap();

    class Builder extends ImmutableContestManagersAddResponse.Builder {}
}
