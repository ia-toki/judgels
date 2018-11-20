package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestScoreboardResponse.class)
public interface ContestScoreboardResponse {
    ContestScoreboard getData();
    Map<String, Profile> getProfilesMap();
    ContestScoreboardConfig getConfig();

    class Builder extends ImmutableContestScoreboardResponse.Builder {}
}
