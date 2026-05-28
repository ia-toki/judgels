package judgels.contest;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import judgels.api.contest.Contest;
import judgels.api.contest.ContestConfig;
import judgels.api.contest.ContestCreateData;
import judgels.api.contest.ContestsResponse;
import judgels.api.contest.module.IcpcStyleModuleConfig;
import judgels.api.contest.role.ContestRole;
import judgels.contest.log.ContestLogger;
import judgels.contest.module.ContestModuleStore;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/admin/contests")
public class ContestAdminResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestRoleChecker contestRoleChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestLogger contestLogger;
    @Inject protected ContestModuleStore moduleStore;

    @Inject public ContestAdminResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestsResponse getContests(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("name") Optional<String> name,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        boolean isAdmin = contestRoleChecker.canAdminister(actorJid);

        Optional<String> userJid = isAdmin ? Optional.empty() : Optional.of(actorJid);
        Page<Contest> contests = contestStore.getContests(userJid, name, pageNumber, PAGE_SIZE);

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
}
