package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.user.UserInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestScoreboardResponse.class)
public interface ContestScoreboardResponse {
    ContestScoreboard getData();
    Map<String, UserInfo> getUsersMap();

    class Builder extends ImmutableContestScoreboardResponse.Builder {}
}
