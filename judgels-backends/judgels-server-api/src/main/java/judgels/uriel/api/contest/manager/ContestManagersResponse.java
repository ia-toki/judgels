package judgels.uriel.api.contest.manager;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestManagersResponse.class)
public interface ContestManagersResponse {
    Page<ContestManager> getData();
    Map<String, Profile> getProfilesMap();
    ContestManagerConfig getConfig();

    class Builder extends ImmutableContestManagersResponse.Builder {}
}
