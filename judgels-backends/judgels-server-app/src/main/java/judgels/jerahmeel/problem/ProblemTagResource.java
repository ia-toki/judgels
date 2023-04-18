package judgels.jerahmeel.problem;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.problem.ProblemTagCategory;
import judgels.jerahmeel.api.problem.ProblemTagOption;
import judgels.jerahmeel.api.problem.ProblemTagService;
import judgels.jerahmeel.api.problem.ProblemTagsResponse;
import judgels.sandalphon.problem.base.tag.ProblemTagStore;

public class ProblemTagResource implements ProblemTagService {
    private final ProblemTagStore tagStore;

    @Inject
    public ProblemTagResource(ProblemTagStore tagStore) {
        this.tagStore = tagStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ProblemTagsResponse getProblemTags() {
        Map<String, Integer> tagCounts = tagStore.getPublicTagCounts();

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
