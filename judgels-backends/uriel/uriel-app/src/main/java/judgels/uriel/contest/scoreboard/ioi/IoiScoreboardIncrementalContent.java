package judgels.uriel.contest.scoreboard.ioi;

import java.util.Map;
import java.util.Optional;
import judgels.uriel.contest.scoreboard.ScoreboardIncrementalContent;
import org.immutables.value.Value;

@Value.Immutable
public interface IoiScoreboardIncrementalContent extends ScoreboardIncrementalContent {
    Map<String, Long> getLastAffectingPenaltiesByContestantJid();
    Map<String, Map<String, Optional<Integer>>> getScoresMapsByContestantJid();
    Map<String, Map<String, Map<Integer, Double>>> getMaxScorePerSubtaskMapsByContestantJid();

    class Builder extends ImmutableIoiScoreboardIncrementalContent.Builder {}
}
