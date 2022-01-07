package judgels.uriel.api.contest.submission.programming;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static judgels.persistence.TestClock.NOW;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_JID;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_A;
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
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_SLUG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import java.util.AbstractMap;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.api.submission.programming.SubmissionInfo;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestService;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemService;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.junit.jupiter.api.Test;

class ContestSubmissionServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestProblemService problemService = createService(ContestProblemService.class);
    private ContestSubmissionService submissionService = createService(
            ContestSubmissionService.class);
    private ContestService contestService = createService(ContestService.class);
    private WebTarget webTarget = createWebTarget();

    @Test
    void end_to_end_flow() {
        Contest contest = createContestWithRoles("contest");

        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), ContestModuleType.REGISTRATION);

        contestantService.registerMyselfAsContestant(USER_A_HEADER, contest.getJid());
        contestantService.registerMyselfAsContestant(USER_B_HEADER, contest.getJid());

        problemService.setProblems(ADMIN_HEADER, contest.getJid(), ImmutableList.of(
                new ContestProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .status(ContestProblemStatus.OPEN)
                        .submissionsLimit(0)
                        .build(),
                new ContestProblemData.Builder()
                        .alias("B")
                        .slug(PROBLEM_2_SLUG)
                        .status(ContestProblemStatus.OPEN)
                        .submissionsLimit(0)
                        .build()));

        // as contestant
        submit(contest.getJid(), USER_A_BEARER_TOKEN, PROBLEM_1_JID);
        submit(contest.getJid(), USER_B_BEARER_TOKEN, PROBLEM_2_JID);

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
        assertThat(submission.getProblemJid()).isEqualTo(PROBLEM_1_JID);
        assertThat(submission.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(submission.getGradingEngine()).isEqualTo("Batch");
        assertThat(submission.getGradingLanguage()).isEqualTo("Cpp11");

        // as supervisor
        response = submissionService.getSubmissions(SUPERVISOR_HEADER, contest.getJid(), empty(), empty(), empty());

        submissions = response.getData().getPage();
        assertThat(submissions.size()).isEqualTo(2);

        config = response.getConfig();
        assertThat(config.getCanSupervise()).isTrue();
        assertThat(config.getUserJids()).containsOnly(CONTESTANT_JID, USER_A_JID, USER_B_JID, SUPERVISOR_JID);
        assertThat(config.getProblemJids()).containsOnly(PROBLEM_1_JID, PROBLEM_2_JID);

        assertThat(response.getProfilesMap()).containsOnlyKeys(CONTESTANT_JID, USER_A_JID, USER_B_JID, SUPERVISOR_JID);
        assertThat(response.getProblemAliasesMap()).containsOnly(
                new AbstractMap.SimpleEntry<>(PROBLEM_1_JID, "A"),
                new AbstractMap.SimpleEntry<>(PROBLEM_2_JID, "B"));

        submissions = submissionService.getSubmissions(
                SUPERVISOR_HEADER, contest.getJid(), of(USER_A), empty(), empty()).getData().getPage();
        assertThat(submissions).hasSize(1);
        assertThat(submissions.get(0).getUserJid()).isEqualTo(USER_A_JID);

        submissions = submissionService.getSubmissions(
                SUPERVISOR_HEADER, contest.getJid(), empty(), of("B"), empty()).getData().getPage();
        assertThat(submissions).hasSize(1);
        assertThat(submissions.get(0).getProblemJid()).isEqualTo(PROBLEM_2_JID);

        // as user

        moduleService.enableModule(MANAGER_HEADER, contest.getJid(), ContestModuleType.REGISTRATION);

        assertThat(submit(contest.getJid(), USER_BEARER_TOKEN, PROBLEM_1_JID).getStatus()).isEqualTo(403);

        assertThatThrownBy(
                () -> submissionService.getSubmissions(USER_HEADER, contest.getJid(), empty(), empty(), empty()))
                .hasFieldOrPropertyWithValue("code", 403);

        // as guest
        assertThatThrownBy(
                () -> submissionService.getSubmissionInfo(contest.getJid(), USER_A_JID, PROBLEM_1_JID))
                .hasFieldOrPropertyWithValue("code", 403);

        assertThatThrownBy(
                () -> submissionService.getSubmissionSourceImage(contest.getJid(), USER_A_JID, PROBLEM_1_JID))
                .hasFieldOrPropertyWithValue("code", 403);

        contestService.updateContest(ADMIN_HEADER, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(NOW.minus(10, HOURS))
                .build());

        SubmissionInfo info = submissionService.getSubmissionInfo(contest.getJid(), USER_A_JID, PROBLEM_1_JID);
        assertThat(info.getId()).isEqualTo(submission.getId());
        assertThat(info.getProfile().getUsername()).isEqualTo(USER_A);

        assertThatThrownBy(
                () -> submissionService.getSubmissionInfo(contest.getJid(), USER_A_JID, PROBLEM_2_JID))
                .hasFieldOrPropertyWithValue("code", 404);

        Response response2 = webTarget.path("/api/v2/contests/submissions/programming/image")
                .queryParam("contestJid", contest.getJid())
                .queryParam("userJid", USER_A_JID)
                .queryParam("problemJid", PROBLEM_1_JID)
                .request()
                .get();
        assertThat(response2.getHeaders().getFirst(CONTENT_TYPE)).isEqualTo("image/jpg");
    }

    private Response submit(String contestJid, String token, String problemJid) {
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MULTIPART_FORM_DATA_TYPE);
        multiPart.bodyPart(new FormDataBodyPart("contestJid", contestJid));
        multiPart.bodyPart(new FormDataBodyPart("problemJid", problemJid));
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
