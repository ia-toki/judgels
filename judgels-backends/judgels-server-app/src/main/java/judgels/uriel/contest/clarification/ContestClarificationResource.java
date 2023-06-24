package judgels.uriel.contest.clarification;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.persistence.api.Page;
import judgels.sandalphon.problem.ProblemClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.clarification.ContestClarification;
import judgels.uriel.api.contest.clarification.ContestClarificationAnswerData;
import judgels.uriel.api.contest.clarification.ContestClarificationConfig;
import judgels.uriel.api.contest.clarification.ContestClarificationData;
import judgels.uriel.api.contest.clarification.ContestClarificationsResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.contest.problem.ContestProblemStore;

@Path("/api/v2/contests/{contestJid}/clarifications")
public class ContestClarificationResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestLogger contestLogger;
    @Inject protected ContestClarificationRoleChecker clarificationRoleChecker;
    @Inject protected ContestClarificationStore clarificationStore;
    @Inject protected ContestProblemStore problemStore;
    @Inject protected UserClient userClient;
    @Inject protected ProblemClient problemClient;

    @Inject public ContestClarificationResource() {}

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestClarification createClarification(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            ContestClarificationData data) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(clarificationRoleChecker.canCreate(actorJid, contest));
        ContestClarification clarification = clarificationStore.createClarification(contestJid, data);

        contestLogger.log(contestJid, "CREATE_CLARIFICATION", clarification.getJid(), clarification.getTopicJid());

        return clarification;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestClarificationsResponse getClarifications(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @QueryParam("status") Optional<String> status,
            @QueryParam("language") Optional<String> language,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(clarificationRoleChecker.canViewOwn(actorJid, contest));

        boolean canSupervise = clarificationRoleChecker.canSupervise(actorJid, contest);

        Optional<String> userFilter = Optional.empty();
        Optional<String> statusFilter = Optional.empty();

        if (canSupervise) {
            statusFilter = status;
        } else {
            userFilter = Optional.of(actorJid);
        }

        Page<ContestClarification> clarifications = clarificationStore.getClarifications(contestJid, userFilter, statusFilter, pageNumber, PAGE_SIZE);

        List<String> problemJidsSortedByAlias;
        Set<String> problemJids;

        boolean canCreate = clarificationRoleChecker.canCreate(actorJid, contest);
        if (canCreate) {
            problemJidsSortedByAlias = problemStore.getOpenProblemJids(contestJid);
            problemJids = ImmutableSet.copyOf(problemJidsSortedByAlias);
        } else {
            problemJidsSortedByAlias = Collections.emptyList();
            problemJids = clarifications.getPage()
                    .stream()
                    .map(ContestClarification::getTopicJid)
                    .filter(topicJid -> !topicJid.equals(contestJid))
                    .collect(Collectors.toSet());
        }

        boolean canManage = clarificationRoleChecker.canManage(actorJid, contest);
        ContestClarificationConfig config = new ContestClarificationConfig.Builder()
                .canCreate(canCreate)
                .canSupervise(canSupervise)
                .canManage(canManage)
                .problemJids(problemJidsSortedByAlias)
                .build();

        Set<String> userJids = clarifications.getPage()
                .stream()
                .flatMap(c -> Stream.of(Optional.of(c.getUserJid()), c.getAnswererJid()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        Map<String, Profile> profilesMap = userClient.getProfiles(userJids, contest.getBeginTime());

        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(contestJid, problemJids);
        Map<String, String> problemNamesMap = problemClient.getProblemNames(problemJids, language);

        contestLogger.log(contestJid, "OPEN_CLARIFICATIONS");

        return new ContestClarificationsResponse.Builder()
                .data(clarifications)
                .config(config)
                .profilesMap(profilesMap)
                .problemAliasesMap(problemAliasesMap)
                .problemNamesMap(problemNamesMap)
                .build();
    }

    @PUT
    @Path("/{clarificationJid}/answer")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestClarification answerClarification(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @PathParam("clarificationJid") String clarificationJid,
            ContestClarificationAnswerData data) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkFound(clarificationStore.getClarification(contestJid, clarificationJid));
        checkAllowed(clarificationRoleChecker.canManage(actorJid, contest));

        ContestClarification clarification = clarificationStore.answerClarification(
                contestJid,
                clarificationJid,
                data.getAnswer());

        contestLogger.log(contestJid, "ANSWER_CLARIFICATION", clarificationJid, clarification.getTopicJid());

        return clarification;
    }
}
