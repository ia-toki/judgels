package judgels.uriel.contest.scoreboard;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardService;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.role.RoleChecker;

public class ContestScoreboardResource implements ContestScoreboardService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ContestStore contestStore;
    private final ContestScoreboardFetcher scoreboardFetcher;

    @Inject
    public ContestScoreboardResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ContestStore contestStore,
            ContestScoreboardFetcher scoreboardFetcher) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.contestStore = contestStore;
        this.scoreboardFetcher = scoreboardFetcher;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestScoreboard getScoreboard(AuthHeader authHeader, String contestJid) {
        // TODO(fushar): this should return frozen scoreboard when necessary
        return getScoreboardOfType(authHeader, contestJid, ContestScoreboardType.OFFICIAL);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestScoreboard getFrozenScoreboard(AuthHeader authHeader, String contestJid) {
        return getScoreboardOfType(authHeader, contestJid, ContestScoreboardType.FROZEN);
    }

    private ContestScoreboard getScoreboardOfType(
            AuthHeader authHeader,
            String contestJid,
            ContestScoreboardType type) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canViewContest(actorJid, contestJid));

        Contest contest = checkFound(contestStore.findContestByJid(contestJid));

        return scoreboardFetcher.fetchScoreboard(contestJid, contest.getStyle(), type);
    }
}
