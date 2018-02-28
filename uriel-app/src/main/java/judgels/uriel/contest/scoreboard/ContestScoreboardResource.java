package judgels.uriel.contest.scoreboard;

import static judgels.service.ServiceUtils.checkFound;
import static judgels.uriel.contest.ContestHacks.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardService;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.contest.ContestStore;

public class ContestScoreboardResource implements ContestScoreboardService {
    private final ContestStore contestStore;
    private final ContestScoreboardStore contestScoreboardStore;

    @Inject
    public ContestScoreboardResource(ContestStore contestStore, ContestScoreboardStore contestScoreboardStore) {
        this.contestStore = contestStore;
        this.contestScoreboardStore = contestScoreboardStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestScoreboard getScoreboard(String contestJid) {
        checkAllowed(checkFound(contestStore.findContestByJid(contestJid)));

        // TODO(fushar): this should return frozen scoreboard when necessary
        return checkFound(contestScoreboardStore.findScoreboard(contestJid, ContestScoreboardType.OFFICIAL));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestScoreboard getFrozenScoreboard(String contestJid) {
        checkAllowed(checkFound(contestStore.findContestByJid(contestJid)));

        return checkFound(contestScoreboardStore.findScoreboard(contestJid, ContestScoreboardType.FROZEN));
    }
}
