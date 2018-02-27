package judgels.uriel.contest.scoreboard;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardService;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;

public class ContestScoreboardResource implements ContestScoreboardService {
    private final ContestScoreboardStore contestScoreboardStore;

    @Inject
    public ContestScoreboardResource(ContestScoreboardStore contestScoreboardStore) {
        this.contestScoreboardStore = contestScoreboardStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestScoreboard getScoreboard(String contestJid) {
        // TODO(fushar): this should return frozen scoreboard when necessary
        return checkFound(contestScoreboardStore.findScoreboard(contestJid, ContestScoreboardType.OFFICIAL));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestScoreboard getFrozenScoreboard(String contestJid) {
        return checkFound(contestScoreboardStore.findScoreboard(contestJid, ContestScoreboardType.FROZEN));
    }
}
