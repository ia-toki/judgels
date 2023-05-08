package judgels.uriel.api.contest.submission.programming;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
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
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestSubmissionServiceIntegrationTests extends BaseUrielServiceIntegrationTests {
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

        updateProblemStatement(managerHeader, problem1, "Problem 1", "text");
    }

    @Test
    void get_submissions() {
        submit(contestantAHeader.getBearerToken(), problem1.getJid());
        submit(contestantBHeader.getBearerToken(), problem2.getJid());

        Map<AuthHeader, List<String>> submissionsMap = new LinkedHashMap<>();
        submissionsMap.put(adminHeader, ImmutableList.of(contestantB.getJid(), contestantA.getJid()));
        submissionsMap.put(managerHeader, ImmutableList.of(contestantB.getJid(), contestantA.getJid()));
        submissionsMap.put(supervisorAHeader, ImmutableList.of(contestantB.getJid(), contestantA.getJid()));
        submissionsMap.put(supervisorBHeader, ImmutableList.of(contestantB.getJid(), contestantA.getJid()));
        submissionsMap.put(contestantAHeader, ImmutableList.of(contestantA.getJid()));
        submissionsMap.put(contestantBHeader, ImmutableList.of(contestantB.getJid()));

        Map<AuthHeader, Boolean> canSuperviseMap = new LinkedHashMap<>();
        canSuperviseMap.put(adminHeader, true);
        canSuperviseMap.put(managerHeader, true);
        canSuperviseMap.put(supervisorAHeader, true);
        canSuperviseMap.put(supervisorBHeader, true);
        canSuperviseMap.put(contestantAHeader, false);
        canSuperviseMap.put(contestantBHeader, false);

        Map<AuthHeader, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(adminHeader, true);
        canManageMap.put(managerHeader, true);
        canManageMap.put(supervisorAHeader, true);
        canManageMap.put(supervisorBHeader, false);
        canManageMap.put(contestantAHeader, false);
        canManageMap.put(contestantBHeader, false);

        Map<AuthHeader, List<String>> profilesKeysMap = new LinkedHashMap<>();
        profilesKeysMap.put(
                adminHeader,
                ImmutableList.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        profilesKeysMap.put(
                managerHeader,
                ImmutableList.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        profilesKeysMap.put(
                supervisorAHeader,
                ImmutableList.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        profilesKeysMap.put(
                supervisorBHeader,
                ImmutableList.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        profilesKeysMap.put(contestantAHeader, ImmutableList.of(contestantA.getJid()));
        profilesKeysMap.put(contestantBHeader, ImmutableList.of(contestantB.getJid()));

        Map<AuthHeader, Map<String, String>> problemAliasesMapMap = new LinkedHashMap<>();
        problemAliasesMapMap.put(adminHeader, ImmutableMap.of(problem1.getJid(), "A", problem2.getJid(), "B"));
        problemAliasesMapMap.put(managerHeader, ImmutableMap.of(problem1.getJid(), "A", problem2.getJid(), "B"));
        problemAliasesMapMap.put(supervisorAHeader, ImmutableMap.of(problem1.getJid(), "A", problem2.getJid(), "B"));
        problemAliasesMapMap.put(supervisorBHeader, ImmutableMap.of(problem1.getJid(), "A", problem2.getJid(), "B"));
        problemAliasesMapMap.put(contestantAHeader, ImmutableMap.of(problem1.getJid(), "A"));
        problemAliasesMapMap.put(contestantBHeader, ImmutableMap.of(problem2.getJid(), "B"));

        Map<AuthHeader, List<String>> userJidsMap = new LinkedHashMap<>();
        userJidsMap.put(
                adminHeader,
                ImmutableList.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        userJidsMap.put(
                managerHeader,
                ImmutableList.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        userJidsMap.put(
                supervisorAHeader,
                ImmutableList.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        userJidsMap.put(
                supervisorBHeader,
                ImmutableList.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        userJidsMap.put(contestantAHeader, ImmutableList.of());
        userJidsMap.put(contestantBHeader, ImmutableList.of());

        Map<AuthHeader, List<String>> problemJidsMap = new LinkedHashMap<>();
        problemJidsMap.put(adminHeader, ImmutableList.of(problem1.getJid(), problem2.getJid()));
        problemJidsMap.put(managerHeader, ImmutableList.of(problem1.getJid(), problem2.getJid()));
        problemJidsMap.put(supervisorAHeader, ImmutableList.of(problem1.getJid(), problem2.getJid()));
        problemJidsMap.put(supervisorBHeader, ImmutableList.of(problem1.getJid(), problem2.getJid()));
        problemJidsMap.put(contestantAHeader, ImmutableList.of());
        problemJidsMap.put(contestantBHeader, ImmutableList.of());

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
        submit(contestantAHeader.getBearerToken(), problem1.getJid()); // .get(3)
        submit(contestantBHeader.getBearerToken(), problem2.getJid()); // .get(2)
        submit(contestantAHeader.getBearerToken(), problem2.getJid()); // .get(1)
        submit(contestantBHeader.getBearerToken(), problem1.getJid()); // .get(0)

        ContestSubmissionsResponse response = submissionService
                .getSubmissions(adminHeader, contest.getJid(), empty(), empty(), empty());

        List<Submission> submissions = response.getData().getPage();

        assertThat(submissionService.getSubmissions(
                adminHeader,
                contest.getJid(),
                of(CONTESTANT_A),
                empty(),
                empty()).getData().getPage())
                .containsExactly(submissions.get(1), submissions.get(3));

        assertThat(submissionService.getSubmissions(
                adminHeader,
                contest.getJid(),
                empty(),
                of("A"),
                empty()).getData().getPage())
                .containsExactly(submissions.get(0), submissions.get(3));

        assertThat(submissionService.getSubmissions(
                adminHeader,
                contest.getJid(),
                of(CONTESTANT_B),
                of("B"),
                empty()).getData().getPage())
                .containsExactly(submissions.get(2));
    }

    @Test
    void get_submission_with_source() {
        submit(contestantAHeader.getBearerToken(), problem1.getJid());
        Submission submission = getSubmission(contestantAHeader);

        assertThat(submission.getUserJid()).isEqualTo(contestantA.getJid());
        assertThat(submission.getProblemJid()).isEqualTo(problem1.getJid());
        assertThat(submission.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(submission.getGradingEngine()).isEqualTo("Batch");
        assertThat(submission.getGradingLanguage()).isEqualTo("Cpp11");

        SubmissionWithSourceResponse response =
                submissionService.getSubmissionWithSourceById(contestantAHeader, submission.getId(), empty());

        assertThat(response.getData().getSubmission()).isEqualTo(submission);
        assertThat(response.getProfile().getUsername()).isEqualTo(CONTESTANT_A);
        assertThat(response.getProblemName()).isEqualTo("Problem 1");
        assertThat(response.getProblemAlias()).isEqualTo("A");
        assertThat(response.getContainerName()).isEqualTo(contest.getName());
    }

    @Test
    void get_submission_info_image() {
        submit(contestantAHeader.getBearerToken(), problem1.getJid());
        Submission submission = getSubmission(contestantAHeader);

        endContest(contest);

        SubmissionInfo info = submissionService.getSubmissionInfo(contest.getJid(), contestantA.getJid(), problem1.getJid());
        assertThat(info.getId()).isEqualTo(submission.getId());
        assertThat(info.getProfile().getUsername()).isEqualTo(CONTESTANT_A);

        Response response = webTarget.path("/api/v2/contests/submissions/programming/image")
                .queryParam("contestJid", contest.getJid())
                .queryParam("userJid", contestantA.getJid())
                .queryParam("problemJid", problem1.getJid())
                .request()
                .get();
        assertThat(response.getHeaders().getFirst(CONTENT_TYPE)).isEqualTo("image/jpg");

        response = webTarget.path("/api/v2/contests/submissions/programming/image/dark")
                .queryParam("contestJid", contest.getJid())
                .queryParam("userJid", contestantA.getJid())
                .queryParam("problemJid", problem1.getJid())
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
