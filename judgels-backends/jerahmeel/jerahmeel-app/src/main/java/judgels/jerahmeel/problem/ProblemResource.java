package judgels.jerahmeel.problem;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.problem.ProblemService;
import judgels.jerahmeel.api.problem.ProblemSetProblemInfo;
import judgels.jerahmeel.api.problem.ProblemTagCategory;
import judgels.jerahmeel.api.problem.ProblemTagOption;
import judgels.jerahmeel.api.problem.ProblemTagsResponse;
import judgels.jerahmeel.api.problem.ProblemsResponse;
import judgels.jerahmeel.stats.StatsStore;
import judgels.persistence.api.Page;
import judgels.sandalphon.problem.ProblemClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ProblemResource implements ProblemService {
    private final ActorChecker actorChecker;
    private final ProblemStore problemStore;
    private final StatsStore statsStore;
    private final ProblemClient problemClient;

    @Inject
    public ProblemResource(
            ActorChecker actorChecker,
            ProblemStore problemStore,
            StatsStore statsStore,
            ProblemClient problemClient) {

        this.actorChecker = actorChecker;
        this.problemStore = problemStore;
        this.statsStore = statsStore;
        this.problemClient = problemClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemsResponse getProblems(
            Optional<AuthHeader> authHeader,
            Set<String> tags,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);

        Set<String> allowedProblemJids = null;
        if (!tags.isEmpty()) {
            allowedProblemJids = problemClient.getProblemJidsByTags(tags);
        }

        Page<ProblemSetProblemInfo> problems = problemStore.getProblems(allowedProblemJids, page);
        Set<String> problemJids = problems.getPage().stream()
                .map(ProblemSetProblemInfo::getProblemJid)
                .collect(Collectors.toSet());

        return new ProblemsResponse.Builder()
                .data(problems)
                .problemsMap(problemClient.getProblems(problemJids))
                .problemLevelsMap(problemStore.getProblemLevelsMap(problemJids))
                .problemMetadatasMap(problemClient.getProblemMetadatas(problemJids))
                .problemProgressesMap(statsStore.getProblemProgressesMap(actorJid, problemJids))
                .problemStatsMap(statsStore.getProblemStatsMap(problemJids))
                .build();
    }

    @Override
    public ProblemTagsResponse getProblemTags() {
        return new ProblemTagsResponse.Builder()
                .addData(new ProblemTagCategory.Builder()
                        .title("Statement")
                        .addOptions(new ProblemTagOption.Builder()
                                .label("has English statement")
                                .value("statement-en")
                                .build())
                        .build())
                .addData(new ProblemTagCategory.Builder()
                        .title("Editorial")
                        .addOptions(new ProblemTagOption.Builder()
                                .label("has editorial")
                                .value("editorial-yes")
                                .build())
                        .addOptions(new ProblemTagOption.Builder()
                                .label("has English editorial")
                                .value("editorial-en")
                                .build())
                        .build())
                .addData(new ProblemTagCategory.Builder()
                        .title("Type")
                        .addOptions(new ProblemTagOption.Builder()
                                .label("batch")
                                .value("engine-batch")
                                .build())
                        .addOptions(new ProblemTagOption.Builder()
                                .label("interactive")
                                .value("engine-interactive")
                                .build())
                        .addOptions(new ProblemTagOption.Builder()
                                .label("output only")
                                .value("engine-output-only")
                                .build())
                        .addOptions(new ProblemTagOption.Builder()
                                .label("functional")
                                .value("engine-functional")
                                .build())
                        .build())
                .addData(new ProblemTagCategory.Builder()
                        .title("Scoring")
                        .addOptions(new ProblemTagOption.Builder()
                                .label("partial")
                                .value("scoring-partial")
                                .build())
                        .addOptions(new ProblemTagOption.Builder()
                                .label("has subtasks")
                                .value("scoring-subtasks")
                                .build())
                        .addOptions(new ProblemTagOption.Builder()
                                .label("absolute")
                                .value("scoring-absolute")
                                .build())
                        .build())
                .build();
    }
}
