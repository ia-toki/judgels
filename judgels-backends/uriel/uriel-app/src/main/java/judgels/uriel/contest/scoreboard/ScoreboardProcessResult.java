package judgels.uriel.contest.scoreboard;

import java.util.List;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import org.immutables.value.Value;

@Value.Immutable
public interface ScoreboardProcessResult {
    List<ScoreboardEntry> getEntries();
    ScoreboardIncrementalContent getIncrementalContent();

    class Builder extends ImmutableScoreboardProcessResult.Builder {}
}
