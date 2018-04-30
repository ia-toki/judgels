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

    public Optional<ContestScoreboardResponse> fetchScoreboard(Contest contest, boolean canSuperviseScoreboard) {
        ContestScoreboardType defaultType =
                typeFetcher.fetchViewableTypes(contest.getJid(), canSuperviseScoreboard).get(0);
        return fetchScoreboardOfType(contest, defaultType);
    }

    public Optional<ContestScoreboardResponse> fetchFrozenScoreboard(Contest contest) {
        return fetchScoreboardOfType(contest, ContestScoreboardType.FROZEN);
    }

    private Optional<ContestScoreboardResponse> fetchScoreboardOfType(Contest contest, ContestScoreboardType type) {
        Optional<RawContestScoreboard> rawScoreboard = scoreboardStore.findScoreboard(contest.getJid(), type);

        // TODO(fushar): keep frozen scoreboard in database
        if (type == ContestScoreboardType.FROZEN && !rawScoreboard.isPresent()) {
            rawScoreboard = scoreboardStore.findScoreboard(contest.getJid(), ContestScoreboardType.OFFICIAL);
        }
        return rawScoreboard.map(raw -> responseBuilder.buildResponse(raw, contest.getStyle(), type));
    }
}
