package judgels.uriel.contest.scoreboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import judgels.uriel.api.contest.scoreboard.Scoreboard;

public interface ScoreboardProcessor {
    Scoreboard parseFromString(ObjectMapper mapper, String json);
    Scoreboard filterContestantJids(Scoreboard scoreboard, Set<String> contestantJids);
}
