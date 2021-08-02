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
                        .map(s -> createOption(s.substring("topic-".length()), s, tagCounts))
                        .collect(Collectors.toList()))
                .build();

        return new ProblemTagsResponse.Builder()
                .addData(new ProblemTagCategory.Builder()
                        .title("Statement")
                        .addOptions(createOption("has English statement", "statement-en", tagCounts))
                        .build())
                .addData(new ProblemTagCategory.Builder()
                        .title("Editorial")
                        .addOptions(createOption("has editorial", "editorial-yes", tagCounts))
                        .addOptions(createOption("has English editorial", "editorial-en", tagCounts))
                        .build())
                .addData(topicCategory)
                .addData(new ProblemTagCategory.Builder()
                        .title("Type")
                        .addOptions(createOption("batch", "engine-batch", tagCounts))
                        .addOptions(createOption("interactive", "engine-interactive", tagCounts))
                        .addOptions(createOption("output only", "engine-output-only", tagCounts))
                        .addOptions(createOption("functional", "engine-functional", tagCounts))
                        .build())
                .addData(new ProblemTagCategory.Builder()
                        .title("Scoring")
                        .addOptions(createOption("partial", "scoring-partial", tagCounts))
                        .addOptions(createOption("has subtasks", "scoring-subtasks", tagCounts))
                        .addOptions(createOption("absolute", "scoring-absolute", tagCounts))
                        .build())
                .build();
    }

    private static ProblemTagOption createOption(String name, String value, Map<String, Integer> tagCounts) {
        return new ProblemTagOption.Builder()
                .label(name)
                .value(value)
                .count(tagCounts.getOrDefault(value, 0))
                .build();
    }
}
