package judgels.api.contest.contestant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestantsRemoveResponse.class)
public interface ContestContestantsRemoveResponse {
    Map<String, Profile> getRemovedContestantProfilesMap();

    class Builder extends ImmutableContestContestantsRemoveResponse.Builder {}
}
