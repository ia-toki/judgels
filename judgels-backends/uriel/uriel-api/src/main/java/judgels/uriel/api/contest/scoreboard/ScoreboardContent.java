package judgels.uriel.api.contest.scoreboard;

import java.util.List;

public interface ScoreboardContent {
    List<? extends ScoreboardEntry> getEntries();
}
