package judgels.uriel.api.contest.contestant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestantsUpsertResponse.class)
public interface ContestContestantsUpsertResponse {
    Map<String, Profile> getInsertedContestantProfilesMap();
    Map<String, Profile> getAlreadyContestantProfilesMap();

    class Builder extends ImmutableContestContestantsUpsertResponse.Builder {}
}
