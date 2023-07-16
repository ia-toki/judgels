package judgels.uriel.api;

import static judgels.uriel.api.contest.clarification.ContestClarificationStatus.ANSWERED;
import static judgels.uriel.api.contest.clarification.ContestClarificationStatus.ASKED;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import judgels.uriel.ContestClarificationClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestErrors;
import judgels.uriel.api.contest.clarification.ContestClarification;
import judgels.uriel.api.contest.clarification.ContestClarificationAnswerData;
import judgels.uriel.api.contest.clarification.ContestClarificationData;
import judgels.uriel.api.contest.clarification.ContestClarificationStatus;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestClarificationApiIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestClarificationClient clarificationClient = createClient(ContestClarificationClient.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .begun()
                .supervisorWithManagementPermissions(SUPERVISOR_A, SupervisorManagementPermission.CLARIFICATION)
                .supervisors(SUPERVISOR_B)
                .contestants(CONTESTANT_A, CONTESTANT_B)
                .modules(REGISTRATION, CLARIFICATION)
                .problems("A", PROBLEM_1_SLUG, "B", PROBLEM_2_SLUG)
                .build();

        updateProblemStatement(managerToken, problem1, "Problem 1", "text");
        updateProblemStatement(managerToken, problem2, "Problem 2", "text");
    }

    @Test
    void create_answer_clarification() {
        ContestClarification clarification = clarificationClient.createClarification(
                contestantAToken,
                contest.getJid(),
                new ContestClarificationData.Builder()
                        .topicJid(contest.getJid())
                        .title("Snack")
                        .question("Is snack provided?")
                        .build());

        assertThat(clarification.getUserJid()).isEqualTo(contestantA.getJid());
        assertThat(clarification.getTopicJid()).isEqualTo(contest.getJid());
        assertThat(clarification.getTitle()).isEqualTo("Snack");
        assertThat(clarification.getQuestion()).isEqualTo("Is snack provided?");
        assertThat(clarification.getStatus()).isEqualTo(ASKED);
        assertThat(clarification.getAnswer()).isEmpty();
        assertThat(clarification.getAnswererJid()).isEmpty();
        assertThat(clarification.getAnsweredTime()).isEmpty();

        ContestClarification answeredClarification = clarificationClient.answerClarification(
                supervisorAToken,
                contest.getJid(),
                clarification.getJid(),
                new ContestClarificationAnswerData.Builder()
                        .answer("Yes!")
                        .build());

        assertThat(answeredClarification.getStatus()).isEqualTo(ANSWERED);
        assertThat(answeredClarification.getAnswer()).contains("Yes!");
        assertThat(answeredClarification.getAnswererJid()).contains(supervisorA.getJid());
        assertThat(answeredClarification.getAnsweredTime()).isPresent();

        assertBadRequest(() -> clarificationClient.answerClarification(
                supervisorAToken,
                contest.getJid(),
                answeredClarification.getJid(),
                new ContestClarificationAnswerData.Builder()
                        .answer("Yes!")
                        .build()))
                .hasMessageContaining(ContestErrors.CLARIFICATION_ALREADY_ANSWERED);
    }

    @Test
    void get_clarifications() {
        ContestClarification clarification1 = createClarification(contestantAToken, problem1.getJid());
        ContestClarification clarification2 = createAnsweredClarification(contestantBToken, contest.getJid());

        Map<String, List<ContestClarification>> clarificationsMap = new LinkedHashMap<>();
        clarificationsMap.put(adminToken, List.of(clarification2, clarification1));
        clarificationsMap.put(managerToken, List.of(clarification2, clarification1));
        clarificationsMap.put(supervisorAToken, List.of(clarification2, clarification1));
        clarificationsMap.put(supervisorBToken, List.of(clarification2, clarification1));
        clarificationsMap.put(contestantAToken, List.of(clarification1));
        clarificationsMap.put(contestantBToken, List.of(clarification2));

        Map<String, Boolean> canCreateMap = new LinkedHashMap<>();
        canCreateMap.put(adminToken, false);
        canCreateMap.put(managerToken, false);
        canCreateMap.put(supervisorAToken, false);
        canCreateMap.put(supervisorBToken, false);
        canCreateMap.put(contestantAToken, true);
        canCreateMap.put(contestantBToken, true);

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
        profilesKeysMap.put(adminToken, List.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid()));
        profilesKeysMap.put(managerToken, List.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid()));
        profilesKeysMap.put(supervisorAToken, List.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid()));
        profilesKeysMap.put(supervisorBToken, List.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid()));
        profilesKeysMap.put(contestantAToken, List.of(contestantA.getJid()));
        profilesKeysMap.put(contestantBToken, List.of(contestantB.getJid(), supervisorA.getJid()));

        Map<String, Map<String, String>> problemAliasesMapMap = new LinkedHashMap<>();
        problemAliasesMapMap.put(adminToken, Map.of(problem1.getJid(), "A"));
        problemAliasesMapMap.put(managerToken, Map.of(problem1.getJid(), "A"));
        problemAliasesMapMap.put(supervisorAToken, Map.of(problem1.getJid(), "A"));
        problemAliasesMapMap.put(supervisorBToken, Map.of(problem1.getJid(), "A"));
        problemAliasesMapMap.put(contestantAToken, Map.of(problem1.getJid(), "A", problem2.getJid(), "B"));
        problemAliasesMapMap.put(contestantBToken, Map.of(problem1.getJid(), "A", problem2.getJid(), "B"));

        Map<String, Map<String, String>> problemNamesMapMap = new LinkedHashMap<>();
        problemNamesMapMap.put(adminToken, Map.of(problem1.getJid(), "Problem 1"));
        problemNamesMapMap.put(managerToken, Map.of(problem1.getJid(), "Problem 1"));
        problemNamesMapMap.put(supervisorAToken, Map.of(problem1.getJid(), "Problem 1"));
        problemNamesMapMap.put(supervisorBToken, Map.of(problem1.getJid(), "Problem 1"));
        problemNamesMapMap.put(contestantAToken, Map.of(problem1.getJid(), "Problem 1", problem2.getJid(), "Problem 2"));
        problemNamesMapMap.put(contestantBToken, Map.of(problem1.getJid(), "Problem 1", problem2.getJid(), "Problem 2"));

        for (String authToken : clarificationsMap.keySet()) {
            var response = clarificationClient.getClarifications(authToken, contest.getJid(), null);

            assertThat(response.getData().getPage()).hasSameElementsAs(clarificationsMap.get(authToken));
            assertThat(response.getConfig().getCanCreate()).isEqualTo(canCreateMap.get(authToken));
            assertThat(response.getConfig().getCanSupervise()).isEqualTo(canSuperviseMap.get(authToken));
            assertThat(response.getConfig().getCanManage()).isEqualTo(canManageMap.get(authToken));
            assertThat(response.getProfilesMap()).containsOnlyKeys(profilesKeysMap.get(authToken));
            assertThat(response.getProblemAliasesMap()).isEqualTo(problemAliasesMapMap.get(authToken));
            assertThat(response.getProblemNamesMap()).isEqualTo(problemNamesMapMap.get(authToken));
        }
    }

    @Test
    void get_clarifications__with_status_filter() {
        ContestClarification clarification1 = createClarification(contestantAToken, problem1.getJid());
        ContestClarification clarification2 = createAnsweredClarification(contestantBToken, contest.getJid());
        ContestClarification clarification3 = createClarification(contestantAToken, contest.getJid());
        ContestClarification clarification4 = createAnsweredClarification(contestantBToken, problem1.getJid());

        Map<ContestClarificationStatus, List<ContestClarification>> clarificationsMap = new LinkedHashMap<>();
        clarificationsMap.put(ASKED, List.of(clarification3, clarification1));
        clarificationsMap.put(ANSWERED, List.of(clarification4, clarification2));

        for (ContestClarificationStatus status : clarificationsMap.keySet()) {
            var params = new ContestClarificationClient.GetClarificationsParams();
            params.status = status.name();

            var response = clarificationClient.getClarifications(supervisorAToken, contest.getJid(), params);
            assertThat(response.getData().getPage()).hasSameElementsAs(clarificationsMap.get(status));
        }
    }

    private ContestClarification createClarification(String authToken, String topicJid) {
        return clarificationClient.createClarification(
                authToken,
                contest.getJid(),
                new ContestClarificationData.Builder()
                        .topicJid(topicJid)
                        .title(randomString())
                        .question(randomString())
                        .build());
    }

    private ContestClarification createAnsweredClarification(String authToken, String topicJid) {
        ContestClarification clarification = createClarification(authToken, topicJid);
        return clarificationClient.answerClarification(
                supervisorAToken,
                contest.getJid(),
                clarification.getJid(),
                new ContestClarificationAnswerData.Builder()
                        .answer(randomString())
                        .build());
    }
}
