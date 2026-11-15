package judgels.api.contest.contestant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestantsAddResponse.class)
public interface ContestContestantsAddResponse {
    Map<String, Profile> getAddedContestantProfilesMap();
    Map<String, Profile> getAlreadyContestantProfilesMap();

    class Builder extends ImmutableContestContestantsAddResponse.Builder {}
}
