package judgels.uriel.api.contest.contestant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import java.util.Set;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestantsResponse.class)
public interface ContestContestantsResponse {
    Set<String> getData();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableContestContestantsResponse.Builder {}
}
