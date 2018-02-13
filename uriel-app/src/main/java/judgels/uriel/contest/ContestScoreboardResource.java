package judgels.uriel.contest;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.uriel.api.contest.ContestScoreboard;
import judgels.uriel.api.contest.ContestScoreboardService;

public class ContestScoreboardResource implements ContestScoreboardService {
    private final ContestScoreboardStore contestScoreboardStore;

    @Inject
    public ContestScoreboardResource(ContestScoreboardStore contestScoreboardStore) {
        this.contestScoreboardStore = contestScoreboardStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestScoreboard getOfficialContestScoreboard(String contestJid) {
        return checkFound(contestScoreboardStore.findContestScoreboard(contestJid, true));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestScoreboard getContestScoreboard(String contestJid) {
        return checkFound(contestScoreboardStore.findContestScoreboard(contestJid, false));
    }

    @Override
    @UnitOfWork
    public ContestScoreboard upsertOfficialContestScoreboard(String contestJid, String scoreboard) {
        return checkFound(contestScoreboardStore.upsertContestScoreboard(contestJid, true, scoreboard));
    }

    @Override
    @UnitOfWork
    public ContestScoreboard upsertContestScoreboard(String contestJid, String scoreboard) {
        return checkFound(contestScoreboardStore.upsertContestScoreboard(contestJid, false, scoreboard));
    }
}
