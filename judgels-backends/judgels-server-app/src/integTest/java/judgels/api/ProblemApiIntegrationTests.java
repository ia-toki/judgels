package judgels.api;

import static judgels.api.catalog.problem.ProblemErrors.SETTER_USERNAMES_NOT_FOUND;
import static judgels.api.catalog.problem.ProblemErrors.SLUG_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.ProblemCreateData;
import judgels.api.catalog.problem.ProblemResponse;
import judgels.api.catalog.problem.ProblemSetterRole;
import judgels.api.catalog.problem.ProblemType;
import judgels.api.catalog.problem.ProblemUpdateData;
import judgels.api.user.User;
import judgels.client.ProblemClient;
import judgels.client.ProblemClient.GetProblemsParams;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProblemApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final ProblemClient problemClient = createClient(ProblemClient.class);

    @BeforeAll
    static void setUpWebTarget() {
        webTarget = createWebTarget();
    }

    @Test
    void end_to_end_flow() {
        User writer = createUser("writer");
        User tester = createUser("tester");

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

        ProblemResponse problemResponse = problemClient.getProblem(adminToken, problemA.getJid());
        assertThat(problemResponse.getData()).isEqualTo(problemA);
        assertThat(problemResponse.getSetterJidsMap()).isEmpty();
        assertThat(problemResponse.getTopicTags()).isEmpty();
        assertThat(problemResponse.getHasLocalChanges()).isFalse();
        assertThat(problemResponse.getConfig().getCanEdit()).isTrue();
        assertThat(problemResponse.getConfig().getCanManage()).isTrue();
        assertThat(problemResponse.getProfilesMap()).containsOnlyKeys(admin.getJid());

        ProblemUpdateData updateData = new ProblemUpdateData.Builder()
                .slug("problem-a-new")
                .additionalNote("This is new problem A")
                .putSetterUsernamesMap(ProblemSetterRole.WRITER, List.of(writer.getUsername()))
                .putSetterUsernamesMap(ProblemSetterRole.TESTER, List.of(tester.getUsername(), writer.getUsername()))
                .addTopicTags("topic-graph", "topic-graph: shortest path")
                .build();

        problemA = problemClient.updateProblem(adminToken, problemA.getJid(), updateData);
        assertThat(problemA.getSlug()).isEqualTo("problem-a-new");
        assertThat(problemA.getAdditionalNote()).isEqualTo("This is new problem A");

        problemResponse = problemClient.getProblem(adminToken, problemA.getJid());
        assertThat(problemResponse.getData()).isEqualTo(problemA);
        assertThat(problemResponse.getSetterJidsMap()).isEqualTo(Map.of(
                ProblemSetterRole.WRITER, List.of(writer.getJid()),
                ProblemSetterRole.TESTER, List.of(tester.getJid(), writer.getJid())));
        assertThat(problemResponse.getTopicTags()).containsExactlyInAnyOrder("topic-graph", "topic-graph: shortest path");
        assertThat(problemResponse.getProfilesMap()).containsOnlyKeys(admin.getJid(), writer.getJid(), tester.getJid());

        assertThat(problemClient.getProblemBySlug(adminToken, "problem-a-new")).isEqualTo(problemA);
        assertNotFound(() -> problemClient.getProblemBySlug(adminToken, "problem-a"));

        String problemAJid = problemA.getJid();

        assertBadRequest(() -> problemClient
                .updateProblem(adminToken, problemAJid, new ProblemUpdateData.Builder()
                        .from(updateData)
                        .slug("problem-b")
                        .build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);

        assertBadRequest(() -> problemClient
                .updateProblem(adminToken, problemAJid, new ProblemUpdateData.Builder()
                        .from(updateData)
                        .putSetterUsernamesMap(ProblemSetterRole.EDITORIALIST, List.of("nonexistent"))
                        .build()))
                .hasMessageContaining(SETTER_USERNAMES_NOT_FOUND);

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
        assertForbidden(() -> problemClient.getProblemBySlug(userToken, "problem-a-new"));
        assertForbidden(() -> problemClient.updateProblem(userToken, problemAJid, updateData));

        problemResponse = problemClient.getProblem(userToken, problemB.getJid());
        assertThat(problemResponse.getData().getSlug()).isEqualTo("problem-b");
        assertThat(problemResponse.getConfig().getCanEdit()).isTrue();
        assertThat(problemResponse.getConfig().getCanManage()).isTrue();

        assertThat(problemClient.getProblemBySlug(userToken, "problem-b").getJid()).isEqualTo(problemB.getJid());

        response = problemClient.getProblems(userToken, new GetProblemsParams());
        assertThat(response.getData().getPage())
                .extracting(Problem::getJid)
                .containsExactly(problemB.getJid());
        assertThat(response.getProfilesMap()).containsOnlyKeys(user.getJid());
    }
}
