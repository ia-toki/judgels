package judgels.uriel.contest;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.jophiel.profile.ProfileStore;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.ActiveContestsResponse;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestConfig;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestDescription;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.ContestsResponse;
import judgels.uriel.api.contest.module.IcpcStyleModuleConfig;
import judgels.uriel.api.contest.role.ContestRole;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.contest.module.ContestModuleStore;

@Path("/api/v2/contests")
public class ContestResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestRoleChecker contestRoleChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestLogger contestLogger;
    @Inject protected ContestModuleStore moduleStore;
    @Inject protected ContestContestantStore contestantStore;
    @Inject protected ProfileStore profileStore;

    @Inject public ContestResource() {}

    @GET
    @Path("/{contestJid}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Contest getContest(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canView(actorJid, contest));
        return contest;
    }

    @POST
    @Path("/{contestJid}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Contest updateContest(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            ContestUpdateData data) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canManage(actorJid, contest));
        contest = contestStore.updateContest(contestJid, data);

        contestLogger.log(contestJid, "UPDATE_CONTEST");

        return contest;
    }

    @GET
    @Path("/slug/{contestSlug}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Contest getContestBySlug(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestSlug") String contestSlug) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestBySlug(contestSlug));

        checkAllowed(contestRoleChecker.canView(actorJid, contest));
        return contest;
    }

    @POST
    @Path("/{contestJid}/virtual")
    @UnitOfWork
    public void startVirtualContest(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canStartVirtual(actorJid, contest));

        contestantStore.startVirtualContest(contestJid, actorJid);

        contestLogger.log(contestJid, "START_VIRTUAL_CONTEST");
    }

    @PUT
    @Path("/{contestJid}/virtual/reset")
    @UnitOfWork
    public void resetVirtualContest(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canResetVirtual(actorJid, contest));

        contestantStore.resetVirtualContest(contestJid);

        contestLogger.log(contestJid, "RESET_VIRTUAL_CONTEST");
    }

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestsResponse getContests(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("name") Optional<String> name,
            @QueryParam("page") Optional<Integer> pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        boolean isAdmin = contestRoleChecker.canAdminister(actorJid);

        Optional<String> userJid = isAdmin ? Optional.empty() : Optional.of(actorJid);
        Page<Contest> contests = contestStore.getContests(userJid, name, pageNumber.orElse(1), PAGE_SIZE);

        Map<String, ContestRole> rolesMap = contests.getPage()
                .stream()
                .collect(Collectors.toMap(
                        Contest::getJid,
                        contest -> contestRoleChecker.getRole(actorJid, contest)));

        boolean canAdminister = contestRoleChecker.canAdminister(actorJid);
        ContestConfig config = new ContestConfig.Builder()
                .canAdminister(canAdminister)
                .build();

        return new ContestsResponse.Builder()
                .data(contests)
                .rolesMap(rolesMap)
                .config(config)
                .build();
    }

    @GET
    @Path("/active")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ActiveContestsResponse getActiveContests(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader) {

        String actorJid = actorChecker.check(authHeader);
        boolean isAdmin = contestRoleChecker.canAdminister(actorJid);

        Optional<String> userJid = isAdmin ? Optional.empty() : Optional.of(actorJid);
        List<Contest> contests = contestStore.getActiveContests(userJid);

        Map<String, ContestRole> rolesMap = contests
                .stream()
                .collect(Collectors.toMap(
                        Contest::getJid,
                        contest -> contestRoleChecker.getRole(actorJid, contest)));

        return new ActiveContestsResponse.Builder()
                .data(contests)
                .rolesMap(rolesMap)
                .build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Contest createContest(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            ContestCreateData data) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(contestRoleChecker.canAdminister(actorJid));

        Contest contest = contestStore.createContest(data);
        moduleStore.upsertIcpcStyleModule(contest.getJid(), new IcpcStyleModuleConfig.Builder().build());

        contestLogger.log(contest.getJid(), "CREATE_CONTEST");

        return contest;
    }

    @GET
    @Path("/{contestJid}/description")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestDescription getContestDescription(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canView(actorJid, contest));

        contestLogger.log(contest.getJid(), "OPEN_CONTEST");

        String description = contestStore.getContestDescription(contest.getJid());
        return new ContestDescription.Builder()
                .description(description)
                .profilesMap(profileStore.parseProfiles(description))
                .build();
    }

    @POST
    @Path("/{contestJid}/description")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestDescription updateContestDescription(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            ContestDescription description) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canManage(actorJid, contest));

        ContestDescription newDescription = contestStore.updateContestDescription(contest.getJid(), description);

        contestLogger.log(contest.getJid(), "UPDATE_DESCRIPTION");

        return newDescription;
    }
}
