package judgels.uriel.contest.log;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.log.ContestLog;
import judgels.uriel.api.contest.log.ContestLogConfig;
import judgels.uriel.api.contest.log.ContestLogsResponse;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;

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
    @Inject protected UserClient userClient;

    @Inject public ContestLogResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestLogsResponse getLogs(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @QueryParam("username") Optional<String> username,
            @QueryParam("problemAlias") Optional<String> problemAlias,
            @QueryParam("page") Optional<Integer> pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canManage(actorJid, contest));

        Optional<String> userJid = username.map(u -> userClient.translateUsernameToJid(u).orElse(""));
        Optional<String> problemJid = problemAlias.map(alias -> problemStore
                .getProblemByAlias(contestJid, alias)
                .map(ContestProblem::getProblemJid)
                .orElse(""));

        Page<ContestLog> logs = logStore.getLogs(contestJid, userJid, problemJid, pageNumber.orElse(1), PAGE_SIZE);

        Set<String> userJids = ImmutableSet.<String>builder()
                .addAll(Lists.transform(logs.getPage(), ContestLog::getUserJid))
                .addAll(contestantStore.getApprovedContestantJids(contestJid))
                .addAll(supervisorStore.getAllSupervisorJids(contestJid))
                .build();

        Map<String, Profile> profilesMap = userClient.getProfiles(userJids, contest.getBeginTime());

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
