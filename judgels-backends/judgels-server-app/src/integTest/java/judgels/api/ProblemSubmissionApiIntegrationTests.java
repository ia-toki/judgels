package judgels.api;

import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.assertj.core.api.Assertions.assertThat;

import feign.form.FormData;
import java.util.List;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.PartnerPermission;
import judgels.api.catalog.problem.ProblemStatement;
import judgels.api.catalog.problem.partner.ProblemPartnerData;
import judgels.api.submission.programming.ProblemSubmissionWithSourceResponse;
import judgels.api.submission.programming.ProblemSubmissionsResponse;
import judgels.api.submission.programming.Submission;
import judgels.api.user.User;
import judgels.client.ProblemClient;
import judgels.client.ProblemPartnerClient;
import judgels.client.ProblemStatementClient;
import judgels.client.ProblemSubmissionClient;
import judgels.client.ProblemVersionClient;
import org.junit.jupiter.api.Test;

class ProblemSubmissionApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final ProblemClient problemClient = createClient(ProblemClient.class);
    private final ProblemPartnerClient partnerClient = createClient(ProblemPartnerClient.class);
    private final ProblemStatementClient statementClient = createClient(ProblemStatementClient.class);
    private final ProblemSubmissionClient submissionClient = createClient(ProblemSubmissionClient.class);
    private final ProblemVersionClient versionClient = createClient(ProblemVersionClient.class);

    @Test
    void submit_get_regrade_submissions() {
        User viewer = createUser("submission-viewer");
        String viewerToken = getToken(viewer);

        String problemJid = createProblem(adminToken, "submission-problem").getJid();
        String otherProblemJid = createProblem(adminToken, "submission-other-problem").getJid();

        partnerClient.setPartners(adminToken, problemJid, List.of(new ProblemPartnerData.Builder()
                .username(viewer.getUsername())
                .permission(PartnerPermission.VIEW)
                .build()));

        assertThat(submissionClient.getSubmissionConfig(adminToken, problemJid).getSourceKeys())
                .containsOnlyKeys("source");
        assertThat(problemClient.getProblemPermissions(adminToken, problemJid).getReasonNotAllowedToSubmit())
                .isEmpty();

        // submit

        Submission submission = submit(adminToken, problemJid);
        assertThat(submission.getUserJid()).isEqualTo(admin.getJid());
        assertThat(submission.getProblemJid()).isEqualTo(problemJid);
        assertThat(submission.getContainerJid()).isEqualTo(problemJid);
        assertThat(submission.getGradingLanguage()).isEqualTo("Cpp11");

        Submission otherSubmission = submit(adminToken, otherProblemJid);

        // get submissions

        ProblemSubmissionsResponse response = submissionClient.getSubmissions(viewerToken, problemJid);
        assertThat(response.getData().getPage()).extracting(Submission::getJid).containsExactly(submission.getJid());
        assertThat(response.getProfilesMap()).containsOnlyKeys(admin.getJid());

        // get submission

        ProblemSubmissionWithSourceResponse sourceResponse =
                submissionClient.getSubmissionWithSourceById(viewerToken, problemJid, submission.getId());
        assertThat(sourceResponse.getData().getSubmission().getJid()).isEqualTo(submission.getJid());
        assertThat(sourceResponse.getData().getSource().get().getSubmissionFiles()).containsOnlyKeys("source");
        assertThat(sourceResponse.getProfile().getUsername()).isEqualTo(admin.getUsername());

        // A submission is reachable only through its own problem.
        assertNotFound(() -> submissionClient
                .getSubmissionWithSourceById(adminToken, problemJid, otherSubmission.getId()));
        assertNotFound(() -> submissionClient.regradeSubmission(adminToken, problemJid, otherSubmission.getJid()));

        // regrade

        assertPermitted(() -> submissionClient.regradeSubmission(adminToken, problemJid, submission.getJid()));
        assertPermitted(() -> submissionClient.regradeSubmissions(adminToken, problemJid));

        // as viewer

        assertThat(problemClient.getProblemPermissions(viewerToken, problemJid).getReasonNotAllowedToSubmit())
                .isPresent();
        assertForbidden(() -> submit(viewerToken, problemJid));
        assertForbidden(() -> submissionClient.regradeSubmission(viewerToken, problemJid, submission.getJid()));
        assertForbidden(() -> submissionClient.regradeSubmissions(viewerToken, problemJid));

        // as user

        assertForbidden(() -> submissionClient.getSubmissions(userToken, problemJid));
        assertForbidden(() -> submissionClient.getSubmissionConfig(userToken, problemJid));
        assertForbidden(() -> submissionClient.getSubmissionWithSourceById(userToken, problemJid, submission.getId()));
    }

    @Test
    void submit_with_local_changes() {
        String problemJid = createProblem(adminToken, "submission-local-problem").getJid();
        Submission submission = submit(adminToken, problemJid);

        statementClient.updateStatement(adminToken, problemJid, "en-US", new ProblemStatement.Builder()
                .title("Title")
                .text("Text")
                .build());

        // A submission is graded against the committed version, so the actor's local changes would be left out.
        assertThat(problemClient.getProblemPermissions(adminToken, problemJid).getReasonNotAllowedToSubmit())
                .isPresent();
        assertForbidden(() -> submit(adminToken, problemJid));

        assertPermitted(() -> submit(superadminToken, problemJid));
        assertPermitted(() -> submissionClient.regradeSubmission(adminToken, problemJid, submission.getJid()));
        assertPermitted(() -> submissionClient.regradeSubmissions(adminToken, problemJid));

        versionClient.discardVersionLocalChanges(adminToken, problemJid);

        assertPermitted(() -> submit(adminToken, problemJid));
    }

    @Test
    void bundle_problem() {
        String problemJid = createBundleProblem(adminToken, "submission-bundle-problem").getJid();

        assertNotFound(() -> submissionClient.getSubmissions(adminToken, problemJid));
        assertNotFound(() -> submissionClient.getSubmissionConfig(adminToken, problemJid));
        assertNotFound(() -> submit(adminToken, problemJid));
        assertNotFound(() -> submissionClient.regradeSubmissions(adminToken, problemJid));
    }

    private Submission submit(String token, String problemJid) {
        var file = new FormData(MULTIPART_FORM_DATA, "solution.cpp", "int main() {}".getBytes());
        return submissionClient.createSubmission(token, problemJid, "Cpp11", file);
    }
}
