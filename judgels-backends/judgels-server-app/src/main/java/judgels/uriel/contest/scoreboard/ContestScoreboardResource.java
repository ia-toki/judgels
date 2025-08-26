package judgels.uriel.contest.scoreboard;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.Map;
import java.util.Optional;
import judgels.jophiel.JophielClient;
import judgels.jophiel.api.profile.Profile;
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
    @Inject protected JophielClient jophielClient;

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
                    var contestantJids = Lists.transform(scoreboard.getScoreboard().getContent().getEntries(), ScoreboardEntry::getContestantJid);
                    Map<String, Profile> profilesMap = jophielClient.getProfiles(contestantJids, contest.getBeginTime());

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
