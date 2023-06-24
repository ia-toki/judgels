package judgels.uriel.contest.scoreboard;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardConfig;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardResponse;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.contest.submission.ContestSubmissionRoleChecker;

@Path("/api/v2/contests/{contestJid}/scoreboard")
public class ContestScoreboardResource {
    private static final int PAGE_SIZE = 250;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestLogger contestLogger;
    @Inject protected ContestSubmissionRoleChecker submissionRoleChecker;
    @Inject protected ContestScoreboardRoleChecker scoreboardRoleChecker;
    @Inject protected ContestScoreboardFetcher scoreboardFetcher;
    @Inject protected ContestScoreboardPoller scoreboardUpdaterDispatcher;
    @Inject protected ScoreboardIncrementalMarker scoreboardIncrementalMarker;
    @Inject protected UserClient userClient;

    @Inject public ContestScoreboardResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Optional<ContestScoreboardResponse> getScoreboard(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid,
            @QueryParam("frozen") boolean frozen,
            @QueryParam("showClosedProblems") boolean showClosedProblems,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

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
                .fetchScoreboard(contest, actorJid, canSupervise, frozen, showClosedProblems, pageNumber, PAGE_SIZE)
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

    @POST
    @Path("/refresh")
    @UnitOfWork
    public void refreshScoreboard(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(scoreboardRoleChecker.canManage(actorJid, contest));

        scoreboardIncrementalMarker.invalidateMark(contestJid);
        scoreboardUpdaterDispatcher.updateContestAsync(contest);

        contestLogger.log(contestJid, "REFRESH_SCOREBOARD");
    }
}
