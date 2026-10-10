package judgels.api;

import static judgels.api.catalog.problem.ProblemType.BUNDLE;
import static judgels.api.catalog.problem.ProblemType.PROGRAMMING;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import judgels.api.training.archive.ArchiveCreateData;
import judgels.api.training.problem.ProblemSetProblemInfo;
import judgels.api.training.problemset.ProblemSet;
import judgels.api.training.problemset.ProblemSetCreateData;
import judgels.api.training.problemset.problem.ProblemSetProblemData;
import judgels.client.TrainingProblemClient;
import org.junit.jupiter.api.Test;

class TrainingProblemApiIntegrationTests extends BaseTrainingApiIntegrationTests {
    private final TrainingProblemClient trainingProblemClient = createClient(TrainingProblemClient.class);

    @Test
    void get_problems() {
        archiveClient.createArchive(adminToken, new ArchiveCreateData.Builder()
                .slug("archive")
                .name("Archive")
                .category("Category")
                .build());

        ProblemSet problemSet = problemSetClient.createProblemSet(adminToken, new ProblemSetCreateData.Builder()
                .slug("problemset")
                .name("ProblemSet")
                .archiveSlug("archive")
                .build());

        problemSetProblemClient.setProblems(adminToken, problemSet.getJid(), List.of(
                new ProblemSetProblemData.Builder().alias("A").slug(PROBLEM_1_SLUG).type(PROGRAMMING).build(),
                new ProblemSetProblemData.Builder().alias("B").slug(PROBLEM_2_SLUG).type(PROGRAMMING).build(),
                new ProblemSetProblemData.Builder().alias("C").slug(PROBLEM_3_SLUG).type(BUNDLE).build()));

        var response = trainingProblemClient.getProblems(userToken);

        assertThat(response.getData().getPage()).containsExactlyInAnyOrder(
                new ProblemSetProblemInfo.Builder()
                        .problemSetSlug("problemset")
                        .problemAlias("A")
                        .problemJid(problem1.getJid())
                        .build(),
                new ProblemSetProblemInfo.Builder()
                        .problemSetSlug("problemset")
                        .problemAlias("B")
                        .problemJid(problem2.getJid())
                        .build());
        assertThat(response.getProblemsMap()).containsOnlyKeys(problem1.getJid(), problem2.getJid());
        assertThat(response.getProblemsMap().get(problem1.getJid()).getSlug()).contains(PROBLEM_1_SLUG);
    }
}
