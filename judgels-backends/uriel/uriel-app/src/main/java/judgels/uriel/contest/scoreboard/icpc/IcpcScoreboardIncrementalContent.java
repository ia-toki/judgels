package judgels.uriel.contest.scoreboard.icpc;

import java.util.Map;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardProblemState;
import judgels.uriel.contest.scoreboard.ScoreboardIncrementalContent;
import org.immutables.value.Value;

@Value.Immutable
public interface IcpcScoreboardIncrementalContent extends ScoreboardIncrementalContent {
    Map<String, String> getFirstToSolveSubmissionJids();

    Map<String, Long> getLastAcceptedPenaltiesByContestantJid();
    Map<String, Map<String, Integer>> getAttemptsMapsByContestantJid();
    Map<String, Map<String, Long>> getPenaltyMapsByContestantJid();
    Map<String, Map<String, IcpcScoreboardProblemState>> getProblemStateMapsByContestantJid();

    class Builder extends ImmutableIcpcScoreboardIncrementalContent.Builder {}
}
