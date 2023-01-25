package judgels.uriel.contest.clarification;

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
import judgels.uriel.api.contest.clarification.ContestClarificationService;
import judgels.uriel.api.contest.clarification.ContestClarificationsResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.contest.problem.ContestProblemStore;

public class ContestClarificationResource implements ContestClarificationService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestLogger contestLogger;
    private final ContestClarificationRoleChecker clarificationRoleChecker;
    private final ContestClarificationStore clarificationStore;
    private final ContestProblemStore problemStore;
    private final UserClient userClient;
    private final ProblemClient problemClient;

    @Inject
    public ContestClarificationResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestLogger contestLogger,
            ContestClarificationRoleChecker clarificationRoleChecker,
            ContestClarificationStore clarificationStore,
            ContestProblemStore problemStore,
            UserClient userClient,
            ProblemClient problemClient) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.contestLogger = contestLogger;
        this.clarificationRoleChecker = clarificationRoleChecker;
        this.clarificationStore = clarificationStore;
        this.problemStore = problemStore;
        this.userClient = userClient;
        this.problemClient = problemClient;
    }

    @Override
    @UnitOfWork
    public ContestClarification createClarification(
            AuthHeader authHeader,
            String contestJid,
            ContestClarificationData data) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(clarificationRoleChecker.canCreate(actorJid, contest));
        ContestClarification clarification = clarificationStore.createClarification(contestJid, data);

        contestLogger.log(contestJid, "CREATE_CLARIFICATION", clarification.getJid(), clarification.getTopicJid());

        return clarification;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestClarificationsResponse getClarifications(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> status,
            Optional<String> language,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(clarificationRoleChecker.canViewOwn(actorJid, contest));

        boolean canSupervise = clarificationRoleChecker.canSupervise(actorJid, contest);
        Page<ContestClarification> clarifications = canSupervise
                ? clarificationStore.getClarifications(contestJid, status, page)
                : clarificationStore.getClarifications(contestJid, actorJid, page);

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

    @Override
    @UnitOfWork
    public ContestClarification answerClarification(
            AuthHeader authHeader,
            String contestJid,
            String clarificationJid,
            ContestClarificationAnswerData data) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(clarificationRoleChecker.canManage(actorJid, contest));

        ContestClarification clarification = checkFound(clarificationStore.answerClarification(
                contestJid,
                clarificationJid,
                data.getAnswer()));

        contestLogger.log(contestJid, "ANSWER_CLARIFICATION", clarificationJid, clarification.getTopicJid());

        return clarification;
    }
}
