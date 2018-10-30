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
import judgels.jophiel.api.profile.ProfileService;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.SandalphonUtils;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.service.api.client.BasicAuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.clarification.ContestClarification;
import judgels.uriel.api.contest.clarification.ContestClarificationAnswerData;
import judgels.uriel.api.contest.clarification.ContestClarificationConfig;
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
    private final ProfileService profileService;

    @Inject
    public ContestClarificationResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestClarificationRoleChecker clarificationRoleChecker,
            ContestClarificationStore clarificationStore,
            ContestProblemStore problemStore,
            @SandalphonClientAuthHeader BasicAuthHeader sandalphonClientAuthHeader,
            ClientProblemService clientProblemService,
            ProfileService profileService) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.clarificationRoleChecker = clarificationRoleChecker;
        this.clarificationStore = clarificationStore;
        this.problemStore = problemStore;
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
        this.clientProblemService = clientProblemService;
        this.profileService = profileService;
    }

    @Override
    @UnitOfWork
    public ContestClarification createClarification(
            AuthHeader authHeader,
            String contestJid,
            ContestClarificationData clarificationData) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(clarificationRoleChecker.canCreate(actorJid, contest));

        return clarificationStore.createClarification(contestJid, clarificationData);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestClarificationsResponse getClarifications(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> language,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(clarificationRoleChecker.canViewOwn(actorJid, contest));

        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(options::page);

        Page<ContestClarification> data = clarificationRoleChecker.canSupervise(actorJid, contest)
                ? clarificationStore.getClarifications(contestJid, options.build())
                : clarificationStore.getClarifications(contestJid, actorJid, options.build());

        List<String> problemJidsSortedByAlias;
        Set<String> problemJids;

        boolean canCreate = clarificationRoleChecker.canCreate(actorJid, contest);
        if (canCreate) {
            problemJidsSortedByAlias = problemStore.getOpenProblemJids(contestJid);
            problemJids = ImmutableSet.copyOf(problemJidsSortedByAlias);
        } else {
            problemJidsSortedByAlias = Collections.emptyList();
            problemJids = data.getData()
                    .stream()
                    .map(ContestClarification::getTopicJid)
                    .filter(topicJid -> !topicJid.equals(contestJid))
                    .collect(Collectors.toSet());
        }

        boolean canSupervise = clarificationRoleChecker.canSupervise(actorJid, contest);
        ContestClarificationConfig config = new ContestClarificationConfig.Builder()
                .canCreate(canCreate)
                .canSupervise(canSupervise)
                .problemJids(problemJidsSortedByAlias)
                .build();

        Set<String> userJids = data.getData()
                .stream()
                .flatMap(c -> Stream.of(Optional.of(c.getUserJid()), c.getAnswererJid()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        Map<String, Profile> profilesMap = userJids.isEmpty()
                ? Collections.emptyMap()
                : profileService.getProfiles(userJids, contest.getBeginTime());

        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(contestJid, problemJids);

        Map<String, String> problemNamesMap = problemJids.isEmpty()
                ? Collections.emptyMap()
                : clientProblemService.getProblemsByJids(
                        sandalphonClientAuthHeader,
                        problemJids).entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                e -> e.getKey(),
                                e -> SandalphonUtils.getProblemName(e.getValue(), language)));

        return new ContestClarificationsResponse.Builder()
                .data(data)
                .config(config)
                .profilesMap(profilesMap)
                .problemAliasesMap(problemAliasesMap)
                .problemNamesMap(problemNamesMap)
                .build();
    }

    @Override
    @UnitOfWork
    public void answerClarification(
            AuthHeader authHeader,
            String contestJid,
            String clarificationJid,
            ContestClarificationAnswerData clarificationAnswerData) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(clarificationRoleChecker.canSupervise(actorJid, contest));

        checkFound(clarificationStore.updateClarificationAnswer(
                contestJid,
                clarificationJid,
                clarificationAnswerData.getAnswer()));
    }
}
