package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.problem.tag.ProblemTagCategory;
import judgels.api.catalog.problem.tag.ProblemTagOption;
import judgels.api.training.problem.TrainingProblemTagsResponse;
import judgels.client.TrainingProblemTagClient;
import org.junit.jupiter.api.Test;

class TrainingProblemTagApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final TrainingProblemTagClient problemTagClient = createClient(TrainingProblemTagClient.class);

    @Test
    void get_problem_tags() {
        TrainingProblemTagsResponse response = problemTagClient.getProblemTags();

        List<ProblemTagCategory> data = response.getData();
        assertThat(data).extracting(ProblemTagCategory::getTitle)
                .containsExactly("Statement", "Editorial", "Tag", "Type", "Scoring");

        ProblemTagCategory topicCategory = findCategory(data, "Tag");
        assertThat(topicCategory.getOptions()).isEmpty();

        ProblemTagCategory typeCategory = findCategory(data, "Type");
        assertThat(typeCategory.getOptions()).extracting(ProblemTagOption::getValue)
                .containsExactly("engine-batch", "engine-interactive", "engine-output-only", "engine-functional");
        assertThat(typeCategory.getOptions()).allMatch(option -> option.getCount() == 0);
    }

    private static ProblemTagCategory findCategory(List<ProblemTagCategory> data, String title) {
        return data.stream()
                .filter(category -> category.getTitle().equals(title))
                .findFirst()
                .orElseThrow();
    }
}
