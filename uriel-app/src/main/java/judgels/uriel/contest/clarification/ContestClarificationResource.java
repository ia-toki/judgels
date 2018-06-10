package judgels.uriel.contest.clarification;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.sandalphon.SandalphonUtils;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.service.api.client.BasicAuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.clarification.ContestClarification;
import judgels.uriel.api.contest.clarification.ContestClarificationData;
import judgels.uriel.api.contest.clarification.ContestClarificationService;
import judgels.uriel.api.contest.clarification.ContestClarificationsResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.sandalphon.SandalphonClientAuthHeader;

public class ContestClarificationResource implements ContestClarificationService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestClarificationRoleChecker clarificationRoleChecker;
    private final ContestClarificationStore clarificationStore;
    private final ContestProblemStore problemStore;
    private final BasicAuthHeader sandalphonClientAuthHeader;
    private final ClientProblemService clientProblemService;

    @Inject
    public ContestClarificationResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestClarificationRoleChecker clarificationRoleChecker,
            ContestClarificationStore clarificationStore,
            ContestProblemStore problemStore,
            @SandalphonClientAuthHeader BasicAuthHeader sandalphonClientAuthHeader,
            ClientProblemService clientProblemService) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.clarificationRoleChecker = clarificationRoleChecker;
        this.clarificationStore = clarificationStore;
        this.problemStore = problemStore;
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
        this.clientProblemService = clientProblemService;
    }

    @Override
    @UnitOfWork
    public ContestClarification createClarification(
            AuthHeader authHeader,
            String contestJid,
            ContestClarificationData clarificationData) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        checkAllowed(clarificationRoleChecker.canCreateClarification(actorJid, contest));

        return clarificationStore.createClarification(contestJid, clarificationData);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestClarificationsResponse getMyClarifications(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        checkAllowed(clarificationRoleChecker.canViewOwnClarifications(actorJid, contest));

        List<ContestClarification> clarifications = clarificationStore.getClarifications(contestJid, actorJid);
        Set<String> problemJids = clarifications
                .stream()
                .map(ContestClarification::getTopicJid)
                .filter(jid -> !jid.equals(contestJid))
                .collect(Collectors.toSet());
        Map<String, String> problemAliasesMap = problemStore.findProblemAliasesByJids(contestJid, problemJids);
        Map<String, String> problemNamesMap = clientProblemService.findProblemsByJids(
                sandalphonClientAuthHeader,
                problemJids).entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> SandalphonUtils.getProblemName(e.getValue(), language)));

        return new ContestClarificationsResponse.Builder()
                .data(clarifications)
                .problemAliasesMap(problemAliasesMap)
                .problemNamesMap(problemNamesMap)
                .build();
    }
}
