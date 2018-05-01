package judgels.uriel.contest.scoreboard;

import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardResponse;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;

public class ContestScoreboardFetcher {
    private final ContestScoreboardTypeFetcher typeFetcher;
    private final ContestScoreboardStore scoreboardStore;
    private final ContestScoreboardResponseBuilder responseBuilder;

    @Inject
    public ContestScoreboardFetcher(
            ContestScoreboardTypeFetcher typeFetcher,
            ContestScoreboardStore scoreboardStore,
            ContestScoreboardResponseBuilder responseBuilder) {

        this.typeFetcher = typeFetcher;
        this.scoreboardStore = scoreboardStore;
        this.responseBuilder = responseBuilder;
    }

    public Optional<ContestScoreboardResponse> fetchScoreboard(
            Contest contest,
            String userJid,
            boolean canSuperviseScoreboard) {

        ContestScoreboardType defaultType =
                typeFetcher.fetchViewableTypes(contest.getJid(), canSuperviseScoreboard).get(0);
        return fetchScoreboardOfType(contest, userJid, defaultType);
    }

    public Optional<ContestScoreboardResponse> fetchFrozenScoreboard(Contest contest, String userJid) {
        return fetchScoreboardOfType(contest, userJid, ContestScoreboardType.FROZEN);
    }

    private Optional<ContestScoreboardResponse> fetchScoreboardOfType(
            Contest contest,
            String userJid,
            ContestScoreboardType type) {

        Optional<RawContestScoreboard> rawScoreboard = scoreboardStore.findScoreboard(contest.getJid(), type);
        ContestScoreboardType actualType;

        // TODO(fushar): keep frozen scoreboard in database
        if (type == ContestScoreboardType.FROZEN && !rawScoreboard.isPresent()) {
            actualType = ContestScoreboardType.OFFICIAL;
            rawScoreboard = scoreboardStore.findScoreboard(contest.getJid(), actualType);
        } else {
            actualType = type;
        }
        return rawScoreboard.map(raw -> responseBuilder.buildResponse(contest, userJid, raw, actualType));
    }
}
