package judgels.contest.log;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.api.contest.Contest;
import judgels.api.contest.log.ContestLog;
import judgels.api.contest.log.ContestLogConfig;
import judgels.api.contest.log.ContestLogsResponse;
import judgels.api.contest.problem.ContestProblem;
import judgels.api.profile.Profile;
import judgels.contest.ContestRoleChecker;
import judgels.contest.ContestStore;
import judgels.contest.contestant.ContestContestantStore;
import judgels.contest.problem.ContestProblemStore;
import judgels.contest.supervisor.ContestSupervisorStore;
import judgels.persistence.api.Page;
import judgels.profile.ProfileStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.user.UserStore;

@Path("/api/v2/contests/{contestJid}/logs")
public class ContestLogResource {
    private static final int PAGE_SIZE = 100;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestRoleChecker contestRoleChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestLogStore logStore;
    @Inject protected ContestContestantStore contestantStore;
    @Inject protected ContestSupervisorStore supervisorStore;
    @Inject protected ContestProblemStore problemStore;
    @Inject protected ProfileStore profileStore;
    @Inject protected UserStore userStore;

    @Inject public ContestLogResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestLogsResponse getLogs(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @QueryParam("username") Optional<String> username,
            @QueryParam("problemAlias") Optional<String> problemAlias,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canManage(actorJid, contest));

        Optional<String> userJid = username.map(u -> userStore.translateUsernameToJid(u).orElse(""));
        Optional<String> problemJid = problemAlias.map(alias -> problemStore
                .getProblemByAlias(contestJid, alias)
                .map(ContestProblem::getProblemJid)
                .orElse(""));

        Page<ContestLog> logs = logStore.getLogs(contestJid, userJid, problemJid, pageNumber, PAGE_SIZE);

        Set<String> userJids = ImmutableSet.<String>builder()
                .addAll(Lists.transform(logs.getPage(), ContestLog::getUserJid))
                .addAll(contestantStore.getApprovedContestantJids(contestJid))
                .addAll(supervisorStore.getAllSupervisorJids(contestJid))
                .build();

        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids, contest.getBeginTime());

        List<String> userJidsSortedByUsername = Lists.newArrayList(userJids);
        userJidsSortedByUsername.sort((u1, u2) -> {
            String usernameA = profilesMap.containsKey(u1) ? profilesMap.get(u1).getUsername() : u1;
            String usernameB = profilesMap.containsKey(u2) ? profilesMap.get(u2).getUsername() : u2;
            return usernameA.compareTo(usernameB);
        });

        List<String> problemJidsSortedByAlias = problemStore.getProblemJids(contestJid);
        Set<String> problemJids = ImmutableSet.copyOf(problemJidsSortedByAlias);
        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(contest.getJid(), problemJids);

        ContestLogConfig config = new ContestLogConfig.Builder()
                .userJids(userJids)
                .problemJids(problemJids)
                .build();

        return new ContestLogsResponse.Builder()
                .data(logs)
                .config(config)
                .profilesMap(profilesMap)
                .problemAliasesMap(problemAliasesMap)
                .build();
    }
}
