package judgels.api;

import static judgels.api.catalog.problem.ProblemErrors.SLUG_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.ProblemCreateData;
import judgels.api.catalog.problem.ProblemType;
import judgels.api.catalog.problem.ProblemUpdateData;
import judgels.client.ProblemClient;
import judgels.client.ProblemClient.GetProblemsParams;
import judgels.client.ProblemTagClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProblemApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final ProblemClient problemClient = createClient(ProblemClient.class);
    private final ProblemTagClient problemTagClient = createClient(ProblemTagClient.class);

    @BeforeAll
    static void setUpWebTarget() {
        webTarget = createWebTarget();
    }

    @Test
    void end_to_end_flow() {
        // as admin

        Problem problemA = problemClient.createProblem(adminToken, new ProblemCreateData.Builder()
                .slug("problem-a")
                .gradingEngine("Batch")
                .additionalNote("This is problem A")
                .initialLanguage("en-US")
                .build());

        assertThat(problemA.getSlug()).isEqualTo("problem-a");
        assertThat(problemA.getAdditionalNote()).isEqualTo("This is problem A");
        assertThat(problemA.getType()).isEqualTo(ProblemType.PROGRAMMING);
        assertThat(problemA.getAuthorJid()).isEqualTo(admin.getJid());

        Problem problemB = createProblemViaMichael(userToken, "problem-b", "Bundle");

        assertBadRequest(() -> problemClient
                .createProblem(adminToken, new ProblemCreateData.Builder()
                        .slug("problem-a")
                        .gradingEngine("Bundle")
                        .additionalNote("")
                        .initialLanguage("en-US")
                        .build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);

        assertBadRequest(() -> problemClient
                .createProblem(adminToken, new ProblemCreateData.Builder()
                        .slug("problem-c")
                        .gradingEngine("Bogus")
                        .additionalNote("")
                        .initialLanguage("en-US")
                        .build()));

        assertThat(problemClient.getProblem(adminToken, problemA.getJid())).isEqualTo(problemA);
        assertThat(problemClient.getProblemPermissions(adminToken, problemA.getJid()).getCanEdit()).isTrue();
        assertThat(problemClient.getProblemPermissions(adminToken, problemA.getJid()).getCanManage()).isTrue();

        ProblemUpdateData updateData = new ProblemUpdateData.Builder()
                .slug("problem-a-new")
                .additionalNote("This is new problem A")
                .build();

        problemA = problemClient.updateProblem(adminToken, problemA.getJid(), updateData);
        assertThat(problemA.getSlug()).isEqualTo("problem-a-new");
        assertThat(problemA.getAdditionalNote()).isEqualTo("This is new problem A");

        assertThat(problemClient.getProblem(adminToken, problemA.getJid())).isEqualTo(problemA);
        assertThat(problemClient.getProblemBySlug(adminToken, "problem-a-new")).isEqualTo(problemA);
        assertNotFound(() -> problemClient.getProblemBySlug(adminToken, "problem-a"));

        String problemAJid = problemA.getJid();

        assertBadRequest(() -> problemClient
                .updateProblem(adminToken, problemAJid, new ProblemUpdateData.Builder()
                        .from(updateData)
                        .slug("problem-b")
                        .build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);

        var response = problemClient.getProblems(adminToken, new GetProblemsParams());
        assertThat(response.getData().getPage())
                .extracting(Problem::getJid)
                .containsExactlyInAnyOrder(problemA.getJid(), problemB.getJid());
        assertThat(response.getProfilesMap()).containsOnlyKeys(admin.getJid(), user.getJid());

        GetProblemsParams params = new GetProblemsParams();
        params.term = "problem-a";
        response = problemClient.getProblems(adminToken, params);
        assertThat(response.getData().getPage())
                .extracting(Problem::getJid)
                .containsExactly(problemA.getJid());

        problemTagClient.setTopicTags(adminToken, problemAJid, Set.of("topic-graph"));

        params = new GetProblemsParams();
        params.tags = Set.of("topic-graph");
        response = problemClient.getProblems(adminToken, params);
        assertThat(response.getData().getPage())
                .extracting(Problem::getJid)
                .containsExactly(problemA.getJid());

        // as user

        assertForbidden(() -> problemClient
                .createProblem(userToken, new ProblemCreateData.Builder()
                        .slug("problem-c")
                        .gradingEngine("Batch")
                        .additionalNote("")
                        .initialLanguage("en-US")
                        .build()));

        assertForbidden(() -> problemClient.getProblem(userToken, problemAJid));
        assertForbidden(() -> problemClient.getProblemPermissions(userToken, problemAJid));
        assertForbidden(() -> problemClient.getProblemBySlug(userToken, "problem-a-new"));
        assertForbidden(() -> problemClient.updateProblem(userToken, problemAJid, updateData));

        assertThat(problemClient.getProblem(userToken, problemB.getJid()).getSlug()).isEqualTo("problem-b");
        assertThat(problemClient.getProblemPermissions(userToken, problemB.getJid()).getCanEdit()).isTrue();
        assertThat(problemClient.getProblemPermissions(userToken, problemB.getJid()).getCanManage()).isTrue();

        assertThat(problemClient.getProblemBySlug(userToken, "problem-b").getJid()).isEqualTo(problemB.getJid());

        response = problemClient.getProblems(userToken, new GetProblemsParams());
        assertThat(response.getData().getPage())
                .extracting(Problem::getJid)
                .containsExactly(problemB.getJid());
        assertThat(response.getProfilesMap()).containsOnlyKeys(user.getJid());
    }
}
