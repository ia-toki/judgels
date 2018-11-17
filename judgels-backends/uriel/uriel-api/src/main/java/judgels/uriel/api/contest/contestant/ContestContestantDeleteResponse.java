package judgels.uriel.api.contest.contestant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestantDeleteResponse.class)
public interface ContestContestantDeleteResponse {
    Map<String, Profile> getDeletedContestantProfilesMap();

    class Builder extends ImmutableContestContestantDeleteResponse.Builder {}
}
