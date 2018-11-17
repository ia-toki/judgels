package judgels.uriel.api.contest.contestant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestantsResponse.class)
public interface ContestContestantsResponse {
    Page<ContestContestant> getData();
    Map<String, Profile> getProfilesMap();
    ContestContestantConfig getConfig();

    class Builder extends ImmutableContestContestantsResponse.Builder {}
}
