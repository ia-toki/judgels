package judgels.uriel.api;

import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static java.util.stream.Collectors.toList;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static org.assertj.core.api.Assertions.assertThat;

import feign.form.FormData;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.api.submission.programming.SubmissionInfo;
import judgels.sandalphon.api.submission.programming.SubmissionWithSourceResponse;
import judgels.uriel.ContestSubmissionClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestSubmissionApiIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestSubmissionClient submissionClient = createClient(ContestSubmissionClient.class);

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

        updateProblemStatement(managerToken, problem1, "Problem 1", "text");
    }

    @Test
    void get_submissions() {
        submit(contestantAToken, problem1.getJid());
        submit(contestantBToken, problem2.getJid());

        Map<String, List<String>> submissionsMap = new LinkedHashMap<>();
        submissionsMap.put(adminToken, List.of(contestantB.getJid(), contestantA.getJid()));
        submissionsMap.put(managerToken, List.of(contestantB.getJid(), contestantA.getJid()));
        submissionsMap.put(supervisorAToken, List.of(contestantB.getJid(), contestantA.getJid()));
        submissionsMap.put(supervisorBToken, List.of(contestantB.getJid(), contestantA.getJid()));
        submissionsMap.put(contestantAToken, List.of(contestantA.getJid()));
        submissionsMap.put(contestantBToken, List.of(contestantB.getJid()));

        Map<String, Boolean> canSuperviseMap = new LinkedHashMap<>();
        canSuperviseMap.put(adminToken, true);
        canSuperviseMap.put(managerToken, true);
        canSuperviseMap.put(supervisorAToken, true);
        canSuperviseMap.put(supervisorBToken, true);
        canSuperviseMap.put(contestantAToken, false);
        canSuperviseMap.put(contestantBToken, false);

        Map<String, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(adminToken, true);
        canManageMap.put(managerToken, true);
        canManageMap.put(supervisorAToken, true);
        canManageMap.put(supervisorBToken, false);
        canManageMap.put(contestantAToken, false);
        canManageMap.put(contestantBToken, false);

        Map<String, List<String>> profilesKeysMap = new LinkedHashMap<>();
        profilesKeysMap.put(
                adminToken,
                List.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        profilesKeysMap.put(
                managerToken,
                List.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        profilesKeysMap.put(
                supervisorAToken,
                List.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        profilesKeysMap.put(
                supervisorBToken,
                List.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        profilesKeysMap.put(contestantAToken, List.of(contestantA.getJid()));
        profilesKeysMap.put(contestantBToken, List.of(contestantB.getJid()));

        Map<String, Map<String, String>> problemAliasesMapMap = new LinkedHashMap<>();
        problemAliasesMapMap.put(adminToken, Map.of(problem1.getJid(), "A", problem2.getJid(), "B"));
        problemAliasesMapMap.put(managerToken, Map.of(problem1.getJid(), "A", problem2.getJid(), "B"));
        problemAliasesMapMap.put(supervisorAToken, Map.of(problem1.getJid(), "A", problem2.getJid(), "B"));
        problemAliasesMapMap.put(supervisorBToken, Map.of(problem1.getJid(), "A", problem2.getJid(), "B"));
        problemAliasesMapMap.put(contestantAToken, Map.of(problem1.getJid(), "A"));
        problemAliasesMapMap.put(contestantBToken, Map.of(problem2.getJid(), "B"));

        Map<String, List<String>> userJidsMap = new LinkedHashMap<>();
        userJidsMap.put(
                adminToken,
                List.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        userJidsMap.put(
                managerToken,
                List.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        userJidsMap.put(
                supervisorAToken,
                List.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        userJidsMap.put(
                supervisorBToken,
                List.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid(), supervisorB.getJid()));
        userJidsMap.put(contestantAToken, List.of());
        userJidsMap.put(contestantBToken, List.of());

        Map<String, List<String>> problemJidsMap = new LinkedHashMap<>();
        problemJidsMap.put(adminToken, List.of(problem1.getJid(), problem2.getJid()));
        problemJidsMap.put(managerToken, List.of(problem1.getJid(), problem2.getJid()));
        problemJidsMap.put(supervisorAToken, List.of(problem1.getJid(), problem2.getJid()));
        problemJidsMap.put(supervisorBToken, List.of(problem1.getJid(), problem2.getJid()));
        problemJidsMap.put(contestantAToken, List.of());
        problemJidsMap.put(contestantBToken, List.of());

        for (String authToken : submissionsMap.keySet()) {
            var response = submissionClient.getSubmissions(authToken, contest.getJid(), null);

            assertThat(response.getData().getPage().stream().map(Submission::getUserJid).collect(toList()))
                    .isEqualTo(submissionsMap.get(authToken));
            assertThat(response.getProfilesMap()).containsOnlyKeys(profilesKeysMap.get(authToken));
            assertThat(response.getProblemAliasesMap()).isEqualTo(problemAliasesMapMap.get(authToken));

            ContestSubmissionConfig config = response.getConfig();
            assertThat(config.getCanSupervise()).isEqualTo(canSuperviseMap.get(authToken));
            assertThat(config.getCanManage()).isEqualTo(canManageMap.get(authToken));
            assertThat(config.getUserJids()).isEqualTo(userJidsMap.get(authToken));
            assertThat(config.getProblemJids()).isEqualTo(problemJidsMap.get(authToken));
        }
    }

    @Test
    void get_submissions__with_filters() {
        submit(contestantAToken, problem1.getJid()); // .get(3)
        submit(contestantBToken, problem2.getJid()); // .get(2)
        submit(contestantAToken, problem2.getJid()); // .get(1)
        submit(contestantBToken, problem1.getJid()); // .get(0)

        var response = submissionClient.getSubmissions(adminToken, contest.getJid(), null);
        List<Submission> submissions = response.getData().getPage();

        var params = new ContestSubmissionClient.GetSubmissionsParams();
        params.username = CONTESTANT_A;
        assertThat(submissionClient.getSubmissions(adminToken, contest.getJid(), params).getData().getPage())
                .containsExactly(submissions.get(1), submissions.get(3));

        params = new ContestSubmissionClient.GetSubmissionsParams();
        params.problemAlias = "A";
        assertThat(submissionClient.getSubmissions(adminToken, contest.getJid(), params).getData().getPage())
                .containsExactly(submissions.get(0), submissions.get(3));

        params = new ContestSubmissionClient.GetSubmissionsParams();
        params.username = CONTESTANT_B;
        params.problemAlias = "B";
        assertThat(submissionClient.getSubmissions(adminToken, contest.getJid(), params).getData().getPage())
                .containsExactly(submissions.get(2));
    }

    @Test
    void get_submission_with_source() {
        submit(contestantAToken, problem1.getJid());
        Submission submission = getSubmission(contestantAToken);

        assertThat(submission.getUserJid()).isEqualTo(contestantA.getJid());
        assertThat(submission.getProblemJid()).isEqualTo(problem1.getJid());
        assertThat(submission.getContainerJid()).isEqualTo(contest.getJid());
        assertThat(submission.getGradingEngine()).isEqualTo("Batch");
        assertThat(submission.getGradingLanguage()).isEqualTo("Cpp11");

        SubmissionWithSourceResponse response = submissionClient.getSubmissionWithSourceById(contestantAToken, submission.getId());

        assertThat(response.getData().getSubmission()).isEqualTo(submission);
        assertThat(response.getProfile().getUsername()).isEqualTo(CONTESTANT_A);
        assertThat(response.getProblemName()).isEqualTo("Problem 1");
        assertThat(response.getProblemAlias()).isEqualTo("A");
        assertThat(response.getContainerName()).isEqualTo(contest.getName());
    }

    @Test
    void get_submission_info_image() {
        submit(contestantAToken, problem1.getJid());
        Submission submission = getSubmission(contestantAToken);

        endContest(contest);

        SubmissionInfo info = submissionClient.getSubmissionInfo(contest.getJid(), contestantA.getJid(), problem1.getJid());
        assertThat(info.getId()).isEqualTo(submission.getId());
        assertThat(info.getProfile().getUsername()).isEqualTo(CONTESTANT_A);

        var response = submissionClient.getSubmissionSourceImage(contest.getJid(), contestantA.getJid(), problem1.getJid());
        assertThat(response.headers().get(CONTENT_TYPE)).contains("image/jpg");

        response = submissionClient.getSubmissionSourceDarkImage(contest.getJid(), contestantA.getJid(), problem1.getJid());
        assertThat(response.headers().get(CONTENT_TYPE)).contains("image/jpg");
    }

    private void submit(String token, String problemJid) {
        var file = new FormData(MULTIPART_FORM_DATA, "solution.cpp", "int main() {}".getBytes());
        submissionClient.createSubmission(token, contest.getJid(), problemJid, "Cpp11", file);
    }

    private Submission getSubmission(String token) {
        var response = submissionClient.getSubmissions(token, contest.getJid(), null);
        return response.getData().getPage().get(0);
    }
}
