package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.info.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestScoreboardResponse.class)
public interface ContestScoreboardResponse {
    ContestScoreboard getData();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableContestScoreboardResponse.Builder {}
}
