package judgels.uriel.contest.scoreboard;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.user.UserService;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardResponse;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardService;
import judgels.uriel.contest.ContestStore;

public class ContestScoreboardResource implements ContestScoreboardService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestScoreboardRoleChecker scoreboardRoleChecker;
    private final ContestScoreboardFetcher scoreboardFetcher;
    private final UserService userService;

    @Inject
    public ContestScoreboardResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestScoreboardRoleChecker scoreboardRoleChecker,
            ContestScoreboardFetcher scoreboardFetcher,
            UserService userService) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.scoreboardRoleChecker = scoreboardRoleChecker;
        this.scoreboardFetcher = scoreboardFetcher;
        this.userService = userService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Optional<ContestScoreboardResponse> getScoreboard(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(scoreboardRoleChecker.canViewDefaultScoreboard(actorJid, contest));

        return scoreboardFetcher
                .fetchScoreboard(contest, actorJid, scoreboardRoleChecker.canSuperviseScoreboard(actorJid, contest))
                .map(this::buildResponse);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Optional<ContestScoreboardResponse> getFrozenScoreboard(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(scoreboardRoleChecker.canViewDefaultScoreboard(actorJid, contest));

        boolean canSuperviseScoreboard = scoreboardRoleChecker.canSuperviseScoreboard(actorJid, contest);
        return scoreboardFetcher
                .fetchFrozenScoreboard(contest, actorJid, canSuperviseScoreboard)
                .map(this::buildResponse);
    }

    private ContestScoreboardResponse buildResponse(ContestScoreboard scoreboard) {
        return new ContestScoreboardResponse.Builder()
                .data(scoreboard)
                .usersMap(userService.getUsersByJids(scoreboard.getScoreboard().getState().getContestantJids()))
                .build();
    }
}
