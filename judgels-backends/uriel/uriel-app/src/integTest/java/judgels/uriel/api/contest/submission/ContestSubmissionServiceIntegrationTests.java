package judgels.uriel.api.contest.submission;

import static java.util.Optional.empty;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_JID;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import judgels.sandalphon.api.submission.Submission;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemService;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.junit.jupiter.api.Test;

class ContestSubmissionServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestContestantService contestantService = createService(ContestContestantService.class);
    private ContestProblemService problemService = createService(ContestProblemService.class);
    private ContestSubmissionService submissionService = createService(ContestSubmissionService.class);
    private WebTarget webTarget = createWebTarget();

    @Test
    void end_to_end_flow() {
        Contest contest = createContest("contest");

        contestantService.addContestants(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER_A_JID, USER_B_JID));
        problemService.upsertProblem(ADMIN_HEADER, contest.getJid(), new ContestProblemData.Builder()
                .problemJid("problemJid1")
                .alias("A")
                .status(ContestProblemStatus.OPEN)
                .submissionsLimit(0)
                .build());

        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MULTIPART_FORM_DATA_TYPE);
        multiPart.bodyPart(new FormDataBodyPart("contestJid", contest.getJid()));
        multiPart.bodyPart(new FormDataBodyPart("problemJid", PROBLEM_1_JID));
        multiPart.bodyPart(new FormDataBodyPart("gradingLanguage", "Cpp11"));
        multiPart.bodyPart(new FormDataBodyPart(
                FormDataContentDisposition.name("sourceFiles.source").fileName("solution.cpp").build(),
                "int main() {}".getBytes(),
                APPLICATION_OCTET_STREAM_TYPE));

        webTarget
                .path("/api/v2/contests/submissions")
                .request()
                .header(AUTHORIZATION, "Bearer " + USER_A_BEARER_TOKEN)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        ContestSubmissionsResponse response =
                submissionService.getSubmissions(ADMIN_HEADER, contest.getJid(), empty(), empty(), empty());

        Submission submission = response.getData().getPage().get(0);
        assertThat(submission.getUserJid()).isEqualTo(USER_A_JID);
        assertThat(submission.getProblemJid()).isEqualTo("problemJid1");
        assertThat(submission.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(submission.getGradingEngine()).isEqualTo("Batch");
        assertThat(submission.getGradingLanguage()).isEqualTo("Cpp11");
    }
}
