package judgels.uriel.api.contest.submission.programming;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_A;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_A_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_B;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_B_BEARER_TOKEN;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_B_JID;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_SLUG;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.api.submission.programming.SubmissionInfo;
import judgels.sandalphon.api.submission.programming.SubmissionWithSourceResponse;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestSubmissionServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private final ContestSubmissionService submissionService = createService(ContestSubmissionService.class);
    private final WebTarget webTarget = createWebTarget();

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .begun()
                .supervisorWithManagementPermissions(SUPERVISOR_A, SupervisorManagementPermission.SUBMISSION)
                .supervisors(SUPERVISOR_B)
                .contestants(CONTESTANT_A, CONTESTANT_B)
                .problems(
                        "A", PROBLEM_1_SLUG,
                        "B", PROBLEM_2_SLUG)
                .modules(REGISTRATION)
                .build();
    }

    @Test
    void get_submissions() {
        submit(CONTESTANT_A_BEARER_TOKEN, PROBLEM_1_JID);
        submit(CONTESTANT_B_BEARER_TOKEN, PROBLEM_2_JID);

        Map<AuthHeader, List<String>> submissionsMap = new LinkedHashMap<>();
        submissionsMap.put(ADMIN_HEADER, ImmutableList.of(CONTESTANT_B_JID, CONTESTANT_A_JID));
        submissionsMap.put(MANAGER_HEADER, ImmutableList.of(CONTESTANT_B_JID, CONTESTANT_A_JID));
        submissionsMap.put(SUPERVISOR_A_HEADER, ImmutableList.of(CONTESTANT_B_JID, CONTESTANT_A_JID));
        submissionsMap.put(SUPERVISOR_B_HEADER, ImmutableList.of(CONTESTANT_B_JID, CONTESTANT_A_JID));
        submissionsMap.put(CONTESTANT_A_HEADER, ImmutableList.of(CONTESTANT_A_JID));
        submissionsMap.put(CONTESTANT_B_HEADER, ImmutableList.of(CONTESTANT_B_JID));

        Map<AuthHeader, Boolean> canSuperviseMap = new LinkedHashMap<>();
        canSuperviseMap.put(ADMIN_HEADER, true);
        canSuperviseMap.put(MANAGER_HEADER, true);
        canSuperviseMap.put(SUPERVISOR_A_HEADER, true);
        canSuperviseMap.put(SUPERVISOR_B_HEADER, true);
        canSuperviseMap.put(CONTESTANT_A_HEADER, false);
        canSuperviseMap.put(CONTESTANT_B_HEADER, false);

        Map<AuthHeader, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(ADMIN_HEADER, true);
        canManageMap.put(MANAGER_HEADER, true);
        canManageMap.put(SUPERVISOR_A_HEADER, true);
        canManageMap.put(SUPERVISOR_B_HEADER, false);
        canManageMap.put(CONTESTANT_A_HEADER, false);
        canManageMap.put(CONTESTANT_B_HEADER, false);

        Map<AuthHeader, List<String>> profilesKeysMap = new LinkedHashMap<>();
        profilesKeysMap.put(
                ADMIN_HEADER,
                ImmutableList.of(CONTESTANT_A_JID, CONTESTANT_B_JID, SUPERVISOR_A_JID, SUPERVISOR_B_JID));
        profilesKeysMap.put(
                MANAGER_HEADER,
                ImmutableList.of(CONTESTANT_A_JID, CONTESTANT_B_JID, SUPERVISOR_A_JID, SUPERVISOR_B_JID));
        profilesKeysMap.put(
                SUPERVISOR_A_HEADER,
                ImmutableList.of(CONTESTANT_A_JID, CONTESTANT_B_JID, SUPERVISOR_A_JID, SUPERVISOR_B_JID));
        profilesKeysMap.put(
                SUPERVISOR_B_HEADER,
                ImmutableList.of(CONTESTANT_A_JID, CONTESTANT_B_JID, SUPERVISOR_A_JID, SUPERVISOR_B_JID));
        profilesKeysMap.put(CONTESTANT_A_HEADER, ImmutableList.of(CONTESTANT_A_JID));
        profilesKeysMap.put(CONTESTANT_B_HEADER, ImmutableList.of(CONTESTANT_B_JID));

        Map<AuthHeader, Map<String, String>> problemAliasesMapMap = new LinkedHashMap<>();
        problemAliasesMapMap.put(ADMIN_HEADER, ImmutableMap.of(PROBLEM_1_JID, "A", PROBLEM_2_JID, "B"));
        problemAliasesMapMap.put(MANAGER_HEADER, ImmutableMap.of(PROBLEM_1_JID, "A", PROBLEM_2_JID, "B"));
        problemAliasesMapMap.put(SUPERVISOR_A_HEADER, ImmutableMap.of(PROBLEM_1_JID, "A", PROBLEM_2_JID, "B"));
        problemAliasesMapMap.put(SUPERVISOR_B_HEADER, ImmutableMap.of(PROBLEM_1_JID, "A", PROBLEM_2_JID, "B"));
        problemAliasesMapMap.put(CONTESTANT_A_HEADER, ImmutableMap.of(PROBLEM_1_JID, "A"));
        problemAliasesMapMap.put(CONTESTANT_B_HEADER, ImmutableMap.of(PROBLEM_2_JID, "B"));

        Map<AuthHeader, List<String>> userJidsMap = new LinkedHashMap<>();
        userJidsMap.put(
                ADMIN_HEADER,
                ImmutableList.of(CONTESTANT_A_JID, CONTESTANT_B_JID, SUPERVISOR_A_JID, SUPERVISOR_B_JID));
        userJidsMap.put(
                MANAGER_HEADER,
                ImmutableList.of(CONTESTANT_A_JID, CONTESTANT_B_JID, SUPERVISOR_A_JID, SUPERVISOR_B_JID));
        userJidsMap.put(
                SUPERVISOR_A_HEADER,
                ImmutableList.of(CONTESTANT_A_JID, CONTESTANT_B_JID, SUPERVISOR_A_JID, SUPERVISOR_B_JID));
        userJidsMap.put(
                SUPERVISOR_B_HEADER,
                ImmutableList.of(CONTESTANT_A_JID, CONTESTANT_B_JID, SUPERVISOR_A_JID, SUPERVISOR_B_JID));
        userJidsMap.put(CONTESTANT_A_HEADER, ImmutableList.of());
        userJidsMap.put(CONTESTANT_B_HEADER, ImmutableList.of());

        Map<AuthHeader, List<String>> problemJidsMap = new LinkedHashMap<>();
        problemJidsMap.put(ADMIN_HEADER, ImmutableList.of(PROBLEM_1_JID, PROBLEM_2_JID));
        problemJidsMap.put(MANAGER_HEADER, ImmutableList.of(PROBLEM_1_JID, PROBLEM_2_JID));
        problemJidsMap.put(SUPERVISOR_A_HEADER, ImmutableList.of(PROBLEM_1_JID, PROBLEM_2_JID));
        problemJidsMap.put(SUPERVISOR_B_HEADER, ImmutableList.of(PROBLEM_1_JID, PROBLEM_2_JID));
        problemJidsMap.put(CONTESTANT_A_HEADER, ImmutableList.of());
        problemJidsMap.put(CONTESTANT_B_HEADER, ImmutableList.of());

        for (AuthHeader authHeader : submissionsMap.keySet()) {
            ContestSubmissionsResponse response = submissionService
                    .getSubmissions(authHeader, contest.getJid(), empty(), empty(), empty());

            assertThat(response.getData().getPage().stream().map(Submission::getUserJid).collect(toList()))
                    .isEqualTo(submissionsMap.get(authHeader));
            assertThat(response.getProfilesMap()).containsOnlyKeys(profilesKeysMap.get(authHeader));
            assertThat(response.getProblemAliasesMap()).isEqualTo(problemAliasesMapMap.get(authHeader));

            ContestSubmissionConfig config = response.getConfig();
            assertThat(config.getCanSupervise()).isEqualTo(canSuperviseMap.get(authHeader));
            assertThat(config.getCanManage()).isEqualTo(canManageMap.get(authHeader));
            assertThat(config.getUserJids()).isEqualTo(userJidsMap.get(authHeader));
            assertThat(config.getProblemJids()).isEqualTo(problemJidsMap.get(authHeader));
        }
    }

    @Test
    void get_submissions__with_filters() {
        submit(CONTESTANT_A_BEARER_TOKEN, PROBLEM_1_JID); // .get(3)
        submit(CONTESTANT_B_BEARER_TOKEN, PROBLEM_2_JID); // .get(2)
        submit(CONTESTANT_A_BEARER_TOKEN, PROBLEM_2_JID); // .get(1)
        submit(CONTESTANT_B_BEARER_TOKEN, PROBLEM_1_JID); // .get(0)

        ContestSubmissionsResponse response = submissionService
                .getSubmissions(ADMIN_HEADER, contest.getJid(), empty(), empty(), empty());

        List<Submission> submissions = response.getData().getPage();

        assertThat(submissionService.getSubmissions(
                ADMIN_HEADER,
                contest.getJid(),
                of(CONTESTANT_A),
                empty(),
                empty()).getData().getPage())
                .containsExactly(submissions.get(1), submissions.get(3));

        assertThat(submissionService.getSubmissions(
                ADMIN_HEADER,
                contest.getJid(),
                empty(),
                of("A"),
                empty()).getData().getPage())
                .containsExactly(submissions.get(0), submissions.get(3));

        assertThat(submissionService.getSubmissions(
                ADMIN_HEADER,
                contest.getJid(),
                of(CONTESTANT_B),
                of("B"),
                empty()).getData().getPage())
                .containsExactly(submissions.get(2));
    }

    @Test
    void get_submission_with_source() {
        submit(CONTESTANT_A_BEARER_TOKEN, PROBLEM_1_JID);
        Submission submission = getSubmission(CONTESTANT_A_HEADER);

        assertThat(submission.getUserJid()).isEqualTo(CONTESTANT_A_JID);
        assertThat(submission.getProblemJid()).isEqualTo(PROBLEM_1_JID);
        assertThat(submission.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(submission.getGradingEngine()).isEqualTo("Batch");
        assertThat(submission.getGradingLanguage()).isEqualTo("Cpp11");

        SubmissionWithSourceResponse response =
                submissionService.getSubmissionWithSourceById(CONTESTANT_A_HEADER, submission.getId(), empty());

        assertThat(response.getData().getSubmission()).isEqualTo(submission);
        assertThat(response.getProfile().getUsername()).isEqualTo(CONTESTANT_A);
        assertThat(response.getProblemName()).isEqualTo("Problem 1");
        assertThat(response.getProblemAlias()).isEqualTo("A");
        assertThat(response.getContainerName()).isEqualTo(contest.getName());
    }

    @Test
    void get_submission_info_image() {
        submit(CONTESTANT_A_BEARER_TOKEN, PROBLEM_1_JID);
        Submission submission = getSubmission(CONTESTANT_A_HEADER);

        endContest(contest);

        SubmissionInfo info = submissionService.getSubmissionInfo(contest.getJid(), CONTESTANT_A_JID, PROBLEM_1_JID);
        assertThat(info.getId()).isEqualTo(submission.getId());
        assertThat(info.getProfile().getUsername()).isEqualTo(CONTESTANT_A);

        Response response = webTarget.path("/api/v2/contests/submissions/programming/image")
                .queryParam("contestJid", contest.getJid())
                .queryParam("userJid", CONTESTANT_A_JID)
                .queryParam("problemJid", PROBLEM_1_JID)
                .request()
                .get();
        assertThat(response.getHeaders().getFirst(CONTENT_TYPE)).isEqualTo("image/jpg");

        response = webTarget.path("/api/v2/contests/submissions/programming/image/dark")
                .queryParam("contestJid", contest.getJid())
                .queryParam("userJid", CONTESTANT_A_JID)
                .queryParam("problemJid", PROBLEM_1_JID)
                .request()
                .get();
        assertThat(response.getHeaders().getFirst(CONTENT_TYPE)).isEqualTo("image/jpg");
    }

    private Response submit(String token, String problemJid) {
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MULTIPART_FORM_DATA_TYPE);
        multiPart.bodyPart(new FormDataBodyPart("contestJid", contest.getJid()));
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

    private Submission getSubmission(AuthHeader authHeader) {
        ContestSubmissionsResponse response = submissionService
                .getSubmissions(authHeader, contest.getJid(), empty(), empty(), empty());
        return response.getData().getPage().get(0);
    }
}
