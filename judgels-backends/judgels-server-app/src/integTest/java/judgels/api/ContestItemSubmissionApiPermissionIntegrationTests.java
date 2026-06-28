package judgels.api;

import jakarta.ws.rs.core.Form;
import judgels.api.contest.Contest;
import judgels.api.problem.bundle.ItemType;
import judgels.api.submission.bundle.ItemSubmissionData;
import judgels.contest.ContestItemSubmissionClient;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestItemSubmissionApiPermissionIntegrationTests extends BaseContestApiIntegrationTests {
    private static final String PROBLEM_ALIAS = "B";

    private final ContestItemSubmissionClient submissionClient = createClient(ContestItemSubmissionClient.class);

    private Contest contest;
    private String itemJid;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .begun()
                .problems(PROBLEM_ALIAS, PROBLEM_3_SLUG)
                .build();

        updateProblemStatement(managerToken, problem3, "Problem 3", "text");

        Form form = new Form();
        form.param("meta", "1");
        form.param("statement", "<p>QUESTION 1</p>");
        form.param("score", "10");
        itemJid = createBundleProblemItem(managerToken, problem3, ItemType.ESSAY, form);
    }

    @Test
    void get_submissions() {
        assertPermitted(getSubmissions(adminToken));
        assertPermitted(getSubmissions(managerToken));
        assertPermitted(getSubmissions(supervisorToken));
        assertPermitted(getSubmissions(contestantToken));
        assertForbidden(getSubmissions(userToken));
    }

    @Test
    void create_submission() {
        assertPermitted(submit(adminToken));
        assertPermitted(submit(managerToken));
        assertPermitted(submit(supervisorToken));
        assertPermitted(submit(contestantToken));
        assertForbidden(submit(userToken))
                .hasMessageContaining("You are not a contestant");
    }

    @Test
    void regrade_submissions() {
        assertPermitted(regrade(adminToken));
        assertPermitted(regrade(managerToken));
        assertForbidden(regrade(supervisorToken));
        assertForbidden(regrade(contestantToken));
        assertForbidden(regrade(userToken));
    }

    private ThrowingCallable getSubmissions(String token) {
        return () -> submissionClient.getSubmissions(
                token, contest.getJid(), new ContestItemSubmissionClient.GetSubmissionsParams());
    }

    private ThrowingCallable submit(String token) {
        return () -> submissionClient.createItemSubmission(token, new ItemSubmissionData.Builder()
                .containerJid(contest.getJid())
                .problemJid(problem3.getJid())
                .itemJid(itemJid)
                .answer("hello")
                .build());
    }

    private ThrowingCallable regrade(String token) {
        var params = new ContestItemSubmissionClient.RegradeSubmissionsParams();
        params.contestJid = contest.getJid();
        params.problemJid = problem3.getJid();
        return () -> submissionClient.regradeSubmissions(token, params);
    }
}
