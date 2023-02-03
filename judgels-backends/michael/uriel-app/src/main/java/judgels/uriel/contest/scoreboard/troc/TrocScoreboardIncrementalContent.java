package judgels.uriel.contest.scoreboard.troc;

import java.util.Map;
import judgels.uriel.api.contest.scoreboard.TrocScoreboard.TrocScoreboardProblemState;
import judgels.uriel.contest.scoreboard.ScoreboardIncrementalContent;
import org.immutables.value.Value;

@Value.Immutable
public interface TrocScoreboardIncrementalContent extends ScoreboardIncrementalContent {
    Map<String, String> getFirstToSolveSubmissionJids();
    Map<String, Map<String, Integer>> getAttemptsMapsByContestantJid();
    Map<String, Map<String, Long>> getPenaltyMapsByContestantJid();
    Map<String, Map<String, TrocScoreboardProblemState>> getProblemStateMapsByContestantJid();

    class Builder extends ImmutableTrocScoreboardIncrementalContent.Builder {}
}
