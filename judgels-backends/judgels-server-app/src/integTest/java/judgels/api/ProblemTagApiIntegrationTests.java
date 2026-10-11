package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.problem.tag.ProblemTagCategory;
import judgels.api.catalog.problem.tag.ProblemTagOption;
import judgels.api.catalog.problem.tag.ProblemTagsResponse;
import judgels.client.ProblemTagClient;
import org.junit.jupiter.api.Test;

class ProblemTagApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final ProblemTagClient problemTagClient = createClient(ProblemTagClient.class);

    @Test
    void get_and_set_tags() {
        String problemJid = createProblem(adminToken, "problem").getJid();

        assertThat(problemTagClient.getTopicTags(adminToken, problemJid)).isEmpty();

        problemTagClient.setTopicTags(adminToken, problemJid, Set.of("topic-ad hoc", "topic-graph"));
        problemTagClient.setTopicTags(adminToken, problemJid, Set.of("topic-graph", "topic-graph: shortest path"));

        assertThat(problemTagClient.getTopicTags(adminToken, problemJid))
                .containsExactlyInAnyOrder("topic-graph", "topic-graph: shortest path");

        // as admin

        ProblemTagsResponse response = problemTagClient.getTags(adminToken);

        List<ProblemTagCategory> data = response.getData();
        assertThat(data).extracting(ProblemTagCategory::getTitle)
                .containsExactly("Visibility", "Statement", "Editorial", "Tag");

        assertThat(data.get(0).getOptions()).containsExactly(
                new ProblemTagOption.Builder().label("private").value("visibility-private").count(1).build(),
                new ProblemTagOption.Builder().label("public").value("visibility-public").count(0).build());

        assertThat(data.get(3).getOptions()).containsExactly(
                new ProblemTagOption.Builder().label("graph").value("topic-graph").count(1).build(),
                new ProblemTagOption.Builder()
                        .label("graph: shortest path")
                        .value("topic-graph: shortest path")
                        .count(1)
                        .build());

        assertThat(response.getTopicTags()).contains("topic-ad hoc", "topic-graph", "topic-graph: shortest path");

        // as user

        assertForbidden(() -> problemTagClient.getTopicTags(userToken, problemJid));
        assertForbidden(() -> problemTagClient.setTopicTags(userToken, problemJid, Set.of()));

        response = problemTagClient.getTags(userToken);

        assertThat(response.getData()).extracting(ProblemTagCategory::getTitle)
                .containsExactly("Visibility", "Statement", "Editorial");
        assertThat(response.getData().get(0).getOptions()).allMatch(option -> option.getCount() == 0);
        assertThat(response.getTopicTags()).contains("topic-ad hoc");
    }
}
