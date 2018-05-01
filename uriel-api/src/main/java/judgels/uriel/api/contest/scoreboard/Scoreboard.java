package judgels.uriel.api.contest.scoreboard;

import java.util.Set;

public interface Scoreboard {
    ScoreboardState getState();

    Scoreboard filterContestantJids(Set<String> contestantJids);
}
