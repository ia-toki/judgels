package judgels.uriel.contest.scoreboard;

import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;

public class ContestScoreboardFetcher {
    private final ContestScoreboardTypeFetcher typeFetcher;
    private final ContestScoreboardStore scoreboardStore;
    private final ContestScoreboardBuilder scoreboardBuilder;

    @Inject
    public ContestScoreboardFetcher(
            ContestScoreboardTypeFetcher typeFetcher,
            ContestScoreboardStore scoreboardStore,
            ContestScoreboardBuilder scoreboardBuilder) {

        this.typeFetcher = typeFetcher;
        this.scoreboardStore = scoreboardStore;
        this.scoreboardBuilder = scoreboardBuilder;
    }

    public Optional<ContestScoreboard> fetchScoreboard(
            Contest contest,
            String userJid,
            boolean canSuperviseScoreboard) {

        ContestScoreboardType defaultType =
                typeFetcher.fetchViewableTypes(contest.getJid(), canSuperviseScoreboard).get(0);
        return fetchScoreboardOfType(contest, userJid, defaultType, canSuperviseScoreboard);
    }

    public Optional<ContestScoreboard> fetchFrozenScoreboard(
            Contest contest,
            String userJid,
            boolean canSuperviseScoreboard) {
        return fetchScoreboardOfType(contest, userJid, ContestScoreboardType.FROZEN, canSuperviseScoreboard);
    }

    private Optional<ContestScoreboard> fetchScoreboardOfType(
            Contest contest,
            String userJid,
            ContestScoreboardType type,
            boolean canSuperviseScoreboard) {

        Optional<RawContestScoreboard> rawScoreboard = scoreboardStore.getScoreboard(contest.getJid(), type);
        ContestScoreboardType actualType;

        // TODO(fushar): keep frozen scoreboard in database even after being unfrozen
        if (type == ContestScoreboardType.FROZEN && !rawScoreboard.isPresent()) {
            actualType = ContestScoreboardType.OFFICIAL;
            rawScoreboard = scoreboardStore.getScoreboard(contest.getJid(), actualType);
        } else {
            actualType = type;
        }
        return rawScoreboard.map(raw ->
                scoreboardBuilder.buildScoreboard(raw, contest, userJid, actualType, canSuperviseScoreboard));
    }
}
