package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.problem.Problem;
import judgels.problem.ProblemClient;
import judgels.problem.ProblemClient.GetProblemsParams;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProblemApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final ProblemClient problemClient = createClient(ProblemClient.class);

    @BeforeAll
    static void setUpWebTarget() {
        webTarget = createWebTarget();
    }

    @Test
    void get_problems() {
        Problem problemA = createProblem(adminToken, "problem-a");
        Problem problemB = createBundleProblem(userToken, "problem-b");

        // as admin

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

        // as user

        response = problemClient.getProblems(userToken, new GetProblemsParams());
        assertThat(response.getData().getPage())
                .extracting(Problem::getJid)
                .containsExactly(problemB.getJid());
        assertThat(response.getProfilesMap()).containsOnlyKeys(user.getJid());
    }
}
