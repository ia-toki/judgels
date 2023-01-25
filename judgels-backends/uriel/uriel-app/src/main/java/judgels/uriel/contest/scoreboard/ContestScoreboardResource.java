package judgels.uriel.contest.scoreboard;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardConfig;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardResponse;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardService;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.contest.submission.ContestSubmissionRoleChecker;

public class ContestScoreboardResource implements ContestScoreboardService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestLogger contestLogger;
    private final ContestSubmissionRoleChecker submissionRoleChecker;
    private final ContestScoreboardRoleChecker scoreboardRoleChecker;
    private final ContestScoreboardFetcher scoreboardFetcher;
    private final ContestScoreboardPoller scoreboardUpdaterDispatcher;
    private final ScoreboardIncrementalMarker scoreboardIncrementalMarker;
    private final UserClient userClient;
    private static final int PAGE_SIZE = 250;

    @Inject
    public ContestScoreboardResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestLogger contestLogger,
            ContestSubmissionRoleChecker submissionRoleChecker,
            ContestScoreboardRoleChecker scoreboardRoleChecker,
            ContestScoreboardFetcher scoreboardFetcher,
            ContestScoreboardPoller scoreboardUpdaterDispatcher,
            ScoreboardIncrementalMarker scoreboardIncrementalMarker,
            UserClient userClient) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.contestLogger = contestLogger;
        this.submissionRoleChecker = submissionRoleChecker;
        this.scoreboardRoleChecker = scoreboardRoleChecker;
        this.scoreboardFetcher = scoreboardFetcher;
        this.scoreboardUpdaterDispatcher = scoreboardUpdaterDispatcher;
        this.scoreboardIncrementalMarker = scoreboardIncrementalMarker;
        this.userClient = userClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Optional<ContestScoreboardResponse> getScoreboard(
            Optional<AuthHeader> authHeader,
            String contestJid,
            boolean frozen,
            boolean showClosedProblems,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(scoreboardRoleChecker.canViewDefault(actorJid, contest));

        boolean canManage = scoreboardRoleChecker.canManage(actorJid, contest);
        boolean canSupervise = scoreboardRoleChecker.canSupervise(actorJid, contest);
        boolean canViewOfficialAndFrozen = scoreboardRoleChecker.canViewOfficialAndFrozen(actorJid, contest);
        boolean canViewClosedProblems = scoreboardRoleChecker.canViewClosedProblems(actorJid, contest);
        boolean canViewSubmissions = submissionRoleChecker.canViewAll(contest);
        ContestScoreboardConfig config = new ContestScoreboardConfig.Builder()
                .canViewOfficialAndFrozen(canViewOfficialAndFrozen)
                .canViewClosedProblems(canViewClosedProblems)
                .canViewSubmissions(canViewSubmissions)
                .canRefresh(canManage)
                .build();

        if (showClosedProblems) {
            checkAllowed(canSupervise);
        }

        contestLogger.log(contestJid, "OPEN_SCOREBOARD");

        return scoreboardFetcher
                .fetchScoreboard(contest, actorJid, canSupervise, frozen, showClosedProblems, page.orElse(1), PAGE_SIZE)
                .map(scoreboard -> {
                    Set<String> contestantJids = scoreboard.getScoreboard().getContent().getEntries().stream()
                            .map(ScoreboardEntry::getContestantJid).collect(Collectors.toSet());
                    Map<String, Profile> profilesMap =
                            userClient.getProfiles(contestantJids, contest.getBeginTime());

                    return new ContestScoreboardResponse.Builder()
                            .data(scoreboard)
                            .profilesMap(profilesMap)
                            .config(config)
                            .build();
                });
    }

    @Override
    @UnitOfWork
    public void refreshScoreboard(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(scoreboardRoleChecker.canManage(actorJid, contest));

        scoreboardIncrementalMarker.invalidateMark(contestJid);
        scoreboardUpdaterDispatcher.updateContestAsync(contest);

        contestLogger.log(contestJid, "REFRESH_SCOREBOARD");
    }
}
