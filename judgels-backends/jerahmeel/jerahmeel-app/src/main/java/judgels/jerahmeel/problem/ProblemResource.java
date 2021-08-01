package judgels.jerahmeel.problem;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
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
import judgels.jerahmeel.difficulty.ProblemDifficultyStore;
import judgels.jerahmeel.stats.StatsStore;
import judgels.persistence.api.Page;
import judgels.sandalphon.problem.ProblemClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ProblemResource implements ProblemService {
    private final ActorChecker actorChecker;
    private final ProblemStore problemStore;
    private final StatsStore statsStore;
    private final ProblemDifficultyStore difficultyStore;
    private final ProblemClient problemClient;

    @Inject
    public ProblemResource(
            ActorChecker actorChecker,
            ProblemStore problemStore,
            StatsStore statsStore,
            ProblemDifficultyStore difficultyStore,
            ProblemClient problemClient) {

        this.actorChecker = actorChecker;
        this.problemStore = problemStore;
        this.statsStore = statsStore;
        this.difficultyStore = difficultyStore;
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
                .problemMetadatasMap(problemClient.getProblemMetadatas(problemJids))
                .problemDifficultiesMap(difficultyStore.getProblemDifficultiesMap(problemJids))
                .problemProgressesMap(statsStore.getProblemProgressesMap(actorJid, problemJids))
                .build();
    }

    @Override
    public ProblemTagsResponse getProblemTags() {
        Map<String, Integer> tagCounts = problemClient.getPublicTagCounts();

        ProblemTagCategory topicCategory = new ProblemTagCategory.Builder()
                .title("Tag")
                .options(tagCounts.keySet().stream()
                        .filter(s -> s.startsWith("topic-"))
                        .sorted()
                        .map(s -> new ProblemTagOption.Builder()
                                .label(s.substring("topic-".length()))
                                .value(s)
                                .count(tagCounts.get(s))
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return new ProblemTagsResponse.Builder()
                .addData(new ProblemTagCategory.Builder()
                        .title("Statement")
                        .addOptions(new ProblemTagOption.Builder()
                                .label("has English statement")
                                .value("statement-en")
                                .count(tagCounts.getOrDefault("statement-en", 0))
                                .build())
                        .build())
                .addData(new ProblemTagCategory.Builder()
                        .title("Editorial")
                        .addOptions(new ProblemTagOption.Builder()
                                .label("has editorial")
                                .value("editorial-yes")
                                .count(tagCounts.getOrDefault("editorial-yes", 0))
                                .build())
                        .addOptions(new ProblemTagOption.Builder()
                                .label("has English editorial")
                                .value("editorial-en")
                                .count(tagCounts.getOrDefault("editorial-en", 0))
                                .build())
                        .build())
                .addData(topicCategory)
                .addData(new ProblemTagCategory.Builder()
                        .title("Type")
                        .addOptions(new ProblemTagOption.Builder()
                                .label("batch")
                                .value("engine-batch")
                                .count(tagCounts.getOrDefault("engine-batch", 0))
                                .build())
                        .addOptions(new ProblemTagOption.Builder()
                                .label("interactive")
                                .value("engine-interactive")
                                .count(tagCounts.getOrDefault("engine-interactive", 0))
                                .build())
                        .addOptions(new ProblemTagOption.Builder()
                                .label("output only")
                                .value("engine-output-only")
                                .count(tagCounts.getOrDefault("engine-output-only", 0))
                                .build())
                        .addOptions(new ProblemTagOption.Builder()
                                .label("functional")
                                .value("engine-functional")
                                .count(tagCounts.getOrDefault("engine-functional", 0))
                                .build())
                        .build())
                .addData(new ProblemTagCategory.Builder()
                        .title("Scoring")
                        .addOptions(new ProblemTagOption.Builder()
                                .label("partial")
                                .value("scoring-partial")
                                .count(tagCounts.getOrDefault("scoring-partial", 0))
                                .build())
                        .addOptions(new ProblemTagOption.Builder()
                                .label("has subtasks")
                                .value("scoring-subtasks")
                                .count(tagCounts.getOrDefault("scoring-subtasks", 0))
                                .build())
                        .addOptions(new ProblemTagOption.Builder()
                                .label("absolute")
                                .value("scoring-absolute")
                                .count(tagCounts.getOrDefault("scoring-absolute", 0))
                                .build())
                        .build())
                .build();
    }
}
