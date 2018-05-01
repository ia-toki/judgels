package judgels.uriel.contest.scoreboard;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardResponse;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardService;
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
    public Optional<ContestScoreboardResponse> getScoreboard(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canViewScoreboard(actorJid, contestJid));

        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        return scoreboardFetcher.fetchScoreboard(contest, roleChecker.canSuperviseScoreboard(actorJid, contestJid));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Optional<ContestScoreboardResponse> getFrozenScoreboard(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canViewScoreboard(actorJid, contestJid));

        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        return scoreboardFetcher.fetchFrozenScoreboard(contest);
    }
}
