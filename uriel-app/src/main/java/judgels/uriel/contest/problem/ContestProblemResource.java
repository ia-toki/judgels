package judgels.uriel.contest.problem;

import static judgels.sandalphon.SandalphonUtils.combineLanguageRestrictions;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.gabriel.api.LanguageRestriction;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.sandalphon.api.problem.ProblemSubmissionConfig;
import judgels.sandalphon.api.problem.ProblemWorksheet;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.service.api.client.BasicAuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.problem.ContestContestantProblem;
import judgels.uriel.api.contest.problem.ContestContestantProblemWorksheet;
import judgels.uriel.api.contest.problem.ContestContestantProblemsResponse;
import judgels.uriel.api.contest.problem.ContestProblemService;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.style.ContestStyleStore;
import judgels.uriel.role.RoleChecker;
import judgels.uriel.sandalphon.SandalphonClientAuthHeader;

public class ContestProblemResource implements ContestProblemService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ContestStore contestStore;
    private final ContestStyleStore styleStore;
    private final ContestProblemStore problemStore;
    private final BasicAuthHeader sandalphonClientAuthHeader;
    private final ClientProblemService clientProblemService;

    @Inject
    public ContestProblemResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ContestStore contestStore,
            ContestStyleStore styleStore,
            ContestProblemStore problemStore,
            @SandalphonClientAuthHeader BasicAuthHeader sandalphonClientAuthHeader,
            ClientProblemService clientProblemService) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.contestStore = contestStore;
        this.styleStore = styleStore;
        this.problemStore = problemStore;
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
        this.clientProblemService = clientProblemService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestContestantProblemsResponse getMyProblems(
            Optional<AuthHeader> authHeader,
            String contestJid,
            Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        checkAllowed(roleChecker.canViewProblems(actorJid, contest));

        List<ContestContestantProblem> contestantProblems = problemStore.getContestantProblems(contestJid, actorJid);
        Set<String> problemJids =
                contestantProblems.stream().map(p -> p.getProblem().getProblemJid()).collect(Collectors.toSet());

        Map<String, String> problemNamesMap = clientProblemService.findProblemsByJids(
                sandalphonClientAuthHeader,
                language,
                problemJids).entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getName()));

        return new ContestContestantProblemsResponse.Builder()
                .data(contestantProblems)
                .problemNamesMap(problemNamesMap)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestContestantProblemWorksheet getProblemWorksheet(
            Optional<AuthHeader> authHeader,
            String contestJid,
            String problemAlias,
            Optional<String> language) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        checkAllowed(roleChecker.canViewProblems(actorJid, contest));

        ContestContestantProblem contestantProblem =
                checkFound(problemStore.findContestantProblemByAlias(contestJid, actorJid, problemAlias));
        String problemJid = contestantProblem.getProblem().getProblemJid();

        Optional<String> reasonNotAllowedToSubmit = roleChecker.canSubmitProblem(actorJid, contest, contestantProblem);

        ProblemWorksheet worksheet =
                clientProblemService.getProblemWorksheet(sandalphonClientAuthHeader, problemJid, language);

        LanguageRestriction contestGradingLanguageRestriction =
                styleStore.getStyleConfig(contestJid).getGradingLanguageRestriction();
        LanguageRestriction problemGradingLanguageRestriction =
                worksheet.getSubmissionConfig().getGradingLanguageRestriction();
        LanguageRestriction combinedGradingLanguageRestriction =
                combineLanguageRestrictions(contestGradingLanguageRestriction, problemGradingLanguageRestriction);

        ProblemWorksheet finalWorksheet = new ProblemWorksheet.Builder()
                .from(worksheet)
                .submissionConfig(new ProblemSubmissionConfig.Builder()
                        .from(worksheet.getSubmissionConfig())
                        .gradingLanguageRestriction(combinedGradingLanguageRestriction)
                        .build())
                .reasonNotAllowedToSubmit(reasonNotAllowedToSubmit)
                .build();

        return new ContestContestantProblemWorksheet.Builder()
                .contestantProblem(contestantProblem)
                .worksheet(finalWorksheet)
                .build();
    }
}
