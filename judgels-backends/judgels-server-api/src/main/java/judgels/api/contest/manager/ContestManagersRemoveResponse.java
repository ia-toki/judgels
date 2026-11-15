package judgels.api.contest.manager;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestManagersRemoveResponse.class)
public interface ContestManagersRemoveResponse {
    Map<String, Profile> getRemovedManagerProfilesMap();

    class Builder extends ImmutableContestManagersRemoveResponse.Builder {}
}
