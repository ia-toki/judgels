package judgels.uriel.api.contest.submission;

import static com.palantir.conjure.java.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static java.util.Optional.empty;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_JID;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.palantir.conjure.java.api.errors.ErrorType;
import java.util.AbstractMap;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemService;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.api.contest.submission.programming.ContestSubmissionService;
import judgels.uriel.api.contest.submission.programming.ContestSubmissionsResponse;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.junit.jupiter.api.Test;

class ContestSubmissionServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestProblemService problemService = createService(ContestProblemService.class);
    private ContestSubmissionService submissionService = createService(
            ContestSubmissionService.class);
    private WebTarget webTarget = createWebTarget();

    @Test
    void end_to_end_flow() {
        Contest contest = createContestWithRoles("contest");

        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), ContestModuleType.REGISTRATION);

        contestantService.registerMyselfAsContestant(USER_A_HEADER, contest.getJid());
        contestantService.registerMyselfAsContestant(USER_B_HEADER, contest.getJid());

        problemService.setProblems(ADMIN_HEADER, contest.getJid(), ImmutableList.of(new ContestProblemData.Builder()
                .alias("A")
                .slug(PROBLEM_1_SLUG)
                .status(ContestProblemStatus.OPEN)
                .submissionsLimit(0)
                .build()));

        // as contestant
        submit(contest.getJid(), USER_A_BEARER_TOKEN);
        submit(contest.getJid(), USER_B_BEARER_TOKEN);

        ContestSubmissionsResponse response =
                submissionService.getSubmissions(USER_A_HEADER, contest.getJid(), empty(), empty(), empty());

        ContestSubmissionConfig config = response.getConfig();
        assertThat(config.getCanSupervise()).isFalse();
        assertThat(config.getUserJids()).isEmpty();
        assertThat(config.getProblemJids()).isEmpty();

        assertThat(response.getProfilesMap()).containsOnlyKeys(USER_A_JID);
        assertThat(response.getProblemAliasesMap()).containsOnly(new AbstractMap.SimpleEntry<>(PROBLEM_1_JID, "A"));

        List<Submission> submissions = response.getData().getPage();
        assertThat(submissions.size()).isEqualTo(1);

        Submission submission = submissions.get(0);
        assertThat(submission.getUserJid()).isEqualTo(USER_A_JID);
        assertThat(submission.getProblemJid()).isEqualTo("problemJid1");
        assertThat(submission.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(submission.getGradingEngine()).isEqualTo("Batch");
        assertThat(submission.getGradingLanguage()).isEqualTo("Cpp11");

        // as supervisor
        response = submissionService.getSubmissions(SUPERVISOR_HEADER, contest.getJid(), empty(), empty(), empty());

        submissions = response.getData().getPage();
        assertThat(submissions.size()).isEqualTo(2);

        config = response.getConfig();
        assertThat(config.getCanSupervise()).isTrue();
        assertThat(config.getUserJids()).containsOnly(CONTESTANT_JID, USER_A_JID, USER_B_JID);
        assertThat(config.getProblemJids()).containsOnly(PROBLEM_1_JID);

        assertThat(response.getProfilesMap()).containsOnlyKeys(CONTESTANT_JID, USER_A_JID, USER_B_JID);
        assertThat(response.getProblemAliasesMap()).containsOnly(new AbstractMap.SimpleEntry<>(PROBLEM_1_JID, "A"));

        // as user

        moduleService.enableModule(MANAGER_HEADER, contest.getJid(), ContestModuleType.REGISTRATION);

        assertThat(submit(contest.getJid(), USER_BEARER_TOKEN).getStatus()).isEqualTo(403);

        assertThatRemoteExceptionThrownBy(
                () -> submissionService.getSubmissions(USER_HEADER, contest.getJid(), empty(), empty(), empty()))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);
    }

    private Response submit(String contestJid, String token) {
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MULTIPART_FORM_DATA_TYPE);
        multiPart.bodyPart(new FormDataBodyPart("contestJid", contestJid));
        multiPart.bodyPart(new FormDataBodyPart("problemJid", PROBLEM_1_JID));
        multiPart.bodyPart(new FormDataBodyPart("gradingLanguage", "Cpp11"));
        multiPart.bodyPart(new FormDataBodyPart(
                FormDataContentDisposition.name("sourceFiles.source").fileName("solution.cpp").build(),
                "int main() {}".getBytes(),
                APPLICATION_OCTET_STREAM_TYPE));

        return webTarget
                .path("/api/v2/contests/submissions/programming")
                .request()
                .header(AUTHORIZATION, "Bearer " + token)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));
    }
}
