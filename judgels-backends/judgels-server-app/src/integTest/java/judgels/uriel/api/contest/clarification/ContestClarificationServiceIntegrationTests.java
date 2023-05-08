package judgels.uriel.api.contest.clarification;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.clarification.ContestClarificationStatus.ANSWERED;
import static judgels.uriel.api.contest.clarification.ContestClarificationStatus.ASKED;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestErrors;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestClarificationServiceIntegrationTests extends BaseUrielServiceIntegrationTests {
    private final ContestClarificationService clarificationService = createService(ContestClarificationService.class);

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

        updateProblemStatement(managerHeader, problem1, "Problem 1", "text");
        updateProblemStatement(managerHeader, problem2, "Problem 2", "text");
    }

    @Test
    void create_answer_clarification() {
        ContestClarification clarification = clarificationService.createClarification(
                contestantAHeader,
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

        ContestClarification answeredClarification = clarificationService.answerClarification(
                supervisorAHeader,
                contest.getJid(),
                clarification.getJid(),
                new ContestClarificationAnswerData.Builder()
                        .answer("Yes!")
                        .build());

        assertThat(answeredClarification.getStatus()).isEqualTo(ANSWERED);
        assertThat(answeredClarification.getAnswer()).contains("Yes!");
        assertThat(answeredClarification.getAnswererJid()).contains(supervisorA.getJid());
        assertThat(answeredClarification.getAnsweredTime()).isPresent();

        assertBadRequest(() -> clarificationService.answerClarification(
                supervisorAHeader,
                contest.getJid(),
                answeredClarification.getJid(),
                new ContestClarificationAnswerData.Builder()
                        .answer("Yes!")
                        .build()))
                .hasMessageContaining(ContestErrors.CLARIFICATION_ALREADY_ANSWERED);
    }

    @Test
    void get_clarifications() {
        ContestClarification clarification1 = createClarification(contestantAHeader, problem1.getJid());
        ContestClarification clarification2 = createAnsweredClarification(contestantBHeader, contest.getJid());

        Map<AuthHeader, List<ContestClarification>> clarificationsMap = new LinkedHashMap<>();
        clarificationsMap.put(adminHeader, ImmutableList.of(clarification2, clarification1));
        clarificationsMap.put(managerHeader, ImmutableList.of(clarification2, clarification1));
        clarificationsMap.put(supervisorAHeader, ImmutableList.of(clarification2, clarification1));
        clarificationsMap.put(supervisorBHeader, ImmutableList.of(clarification2, clarification1));
        clarificationsMap.put(contestantAHeader, ImmutableList.of(clarification1));
        clarificationsMap.put(contestantBHeader, ImmutableList.of(clarification2));

        Map<AuthHeader, Boolean> canCreateMap = new LinkedHashMap<>();
        canCreateMap.put(adminHeader, false);
        canCreateMap.put(managerHeader, false);
        canCreateMap.put(supervisorAHeader, false);
        canCreateMap.put(supervisorBHeader, false);
        canCreateMap.put(contestantAHeader, true);
        canCreateMap.put(contestantBHeader, true);

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
        profilesKeysMap.put(adminHeader, ImmutableList.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid()));
        profilesKeysMap.put(managerHeader, ImmutableList.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid()));
        profilesKeysMap.put(
                supervisorAHeader, ImmutableList.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid()));
        profilesKeysMap.put(
                supervisorBHeader, ImmutableList.of(contestantA.getJid(), contestantB.getJid(), supervisorA.getJid()));
        profilesKeysMap.put(contestantAHeader, ImmutableList.of(contestantA.getJid()));
        profilesKeysMap.put(contestantBHeader, ImmutableList.of(contestantB.getJid(), supervisorA.getJid()));

        Map<AuthHeader, Map<String, String>> problemAliasesMapMap = new LinkedHashMap<>();
        problemAliasesMapMap.put(adminHeader, ImmutableMap.of(problem1.getJid(), "A"));
        problemAliasesMapMap.put(managerHeader, ImmutableMap.of(problem1.getJid(), "A"));
        problemAliasesMapMap.put(supervisorAHeader, ImmutableMap.of(problem1.getJid(), "A"));
        problemAliasesMapMap.put(supervisorBHeader, ImmutableMap.of(problem1.getJid(), "A"));
        problemAliasesMapMap.put(contestantAHeader, ImmutableMap.of(problem1.getJid(), "A", problem2.getJid(), "B"));
        problemAliasesMapMap.put(contestantBHeader, ImmutableMap.of(problem1.getJid(), "A", problem2.getJid(), "B"));

        Map<AuthHeader, Map<String, String>> problemNamesMapMap = new LinkedHashMap<>();
        problemNamesMapMap.put(adminHeader, ImmutableMap.of(problem1.getJid(), "Problem 1"));
        problemNamesMapMap.put(managerHeader, ImmutableMap.of(problem1.getJid(), "Problem 1"));
        problemNamesMapMap.put(supervisorAHeader, ImmutableMap.of(problem1.getJid(), "Problem 1"));
        problemNamesMapMap.put(supervisorBHeader, ImmutableMap.of(problem1.getJid(), "Problem 1"));
        problemNamesMapMap.put(
                contestantAHeader, ImmutableMap.of(problem1.getJid(), "Problem 1", problem2.getJid(), "Problem 2"));
        problemNamesMapMap.put(
                contestantBHeader, ImmutableMap.of(problem1.getJid(), "Problem 1", problem2.getJid(), "Problem 2"));

        for (AuthHeader authHeader : clarificationsMap.keySet()) {
            ContestClarificationsResponse response =
                    clarificationService.getClarifications(authHeader, contest.getJid(), empty(), empty(), empty());

            assertThat(response.getData().getPage()).hasSameElementsAs(clarificationsMap.get(authHeader));
            assertThat(response.getConfig().getCanCreate()).isEqualTo(canCreateMap.get(authHeader));
            assertThat(response.getConfig().getCanSupervise()).isEqualTo(canSuperviseMap.get(authHeader));
            assertThat(response.getConfig().getCanManage()).isEqualTo(canManageMap.get(authHeader));
            assertThat(response.getProfilesMap()).containsOnlyKeys(profilesKeysMap.get(authHeader));
            assertThat(response.getProblemAliasesMap()).isEqualTo(problemAliasesMapMap.get(authHeader));
            assertThat(response.getProblemNamesMap()).isEqualTo(problemNamesMapMap.get(authHeader));
        }
    }

    @Test
    void get_clarifications__with_status_filter() {
        ContestClarification clarification1 = createClarification(contestantAHeader, problem1.getJid());
        ContestClarification clarification2 = createAnsweredClarification(contestantBHeader, contest.getJid());
        ContestClarification clarification3 = createClarification(contestantAHeader, contest.getJid());
        ContestClarification clarification4 = createAnsweredClarification(contestantBHeader, problem1.getJid());

        Map<ContestClarificationStatus, List<ContestClarification>> clarificationsMap = new LinkedHashMap<>();
        clarificationsMap.put(ASKED, ImmutableList.of(clarification3, clarification1));
        clarificationsMap.put(ANSWERED, ImmutableList.of(clarification4, clarification2));

        for (ContestClarificationStatus status : clarificationsMap.keySet()) {
            ContestClarificationsResponse response = clarificationService
                    .getClarifications(supervisorAHeader, contest.getJid(), of(status.name()), empty(), empty());

            assertThat(response.getData().getPage()).hasSameElementsAs(clarificationsMap.get(status));
        }
    }

    private ContestClarification createClarification(AuthHeader authHeader, String topicJid) {
        return clarificationService.createClarification(
                authHeader,
                contest.getJid(),
                new ContestClarificationData.Builder()
                        .topicJid(topicJid)
                        .title(randomString())
                        .question(randomString())
                        .build());
    }

    private ContestClarification createAnsweredClarification(AuthHeader authHeader, String topicJid) {
        ContestClarification clarification = createClarification(authHeader, topicJid);
        return clarificationService.answerClarification(
                supervisorAHeader,
                contest.getJid(),
                clarification.getJid(),
                new ContestClarificationAnswerData.Builder()
                        .answer(randomString())
                        .build());
    }
}
