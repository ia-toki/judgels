package judgels.uriel.api.contest.clarification;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.clarification.ContestClarificationStatus.ANSWERED;
import static judgels.uriel.api.contest.clarification.ContestClarificationStatus.ASKED;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_A;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_B;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_B_JID;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B_HEADER;
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
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestErrors;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestClarificationServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
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
    }

    @Test
    void create_answer_clarification() {
        ContestClarification clarification = clarificationService.createClarification(
                CONTESTANT_A_HEADER,
                contest.getJid(),
                new ContestClarificationData.Builder()
                        .topicJid(contest.getJid())
                        .title("Snack")
                        .question("Is snack provided?")
                        .build());

        assertThat(clarification.getUserJid()).isEqualTo(CONTESTANT_A_JID);
        assertThat(clarification.getTopicJid()).isEqualTo(contest.getJid());
        assertThat(clarification.getTitle()).isEqualTo("Snack");
        assertThat(clarification.getQuestion()).isEqualTo("Is snack provided?");
        assertThat(clarification.getStatus()).isEqualTo(ASKED);
        assertThat(clarification.getAnswer()).isEmpty();
        assertThat(clarification.getAnswererJid()).isEmpty();
        assertThat(clarification.getAnsweredTime()).isEmpty();

        ContestClarification answeredClarification = clarificationService.answerClarification(
                SUPERVISOR_A_HEADER,
                contest.getJid(),
                clarification.getJid(),
                new ContestClarificationAnswerData.Builder()
                        .answer("Yes!")
                        .build());

        assertThat(answeredClarification.getStatus()).isEqualTo(ANSWERED);
        assertThat(answeredClarification.getAnswer()).contains("Yes!");
        assertThat(answeredClarification.getAnswererJid()).contains(SUPERVISOR_A_JID);
        assertThat(answeredClarification.getAnsweredTime()).isPresent();

        assertBadRequest(() -> clarificationService.answerClarification(
                SUPERVISOR_A_HEADER,
                contest.getJid(),
                answeredClarification.getJid(),
                new ContestClarificationAnswerData.Builder()
                        .answer("Yes!")
                        .build()))
                .hasMessageContaining(ContestErrors.CLARIFICATION_ALREADY_ANSWERED);
    }

    @Test
    void get_clarifications() {
        ContestClarification clarification1 = createClarification(CONTESTANT_A_HEADER, PROBLEM_1_JID);
        ContestClarification clarification2 = createAnsweredClarification(CONTESTANT_B_HEADER, contest.getJid());

        Map<AuthHeader, List<ContestClarification>> clarificationsMap = new LinkedHashMap<>();
        clarificationsMap.put(ADMIN_HEADER, ImmutableList.of(clarification2, clarification1));
        clarificationsMap.put(MANAGER_HEADER, ImmutableList.of(clarification2, clarification1));
        clarificationsMap.put(SUPERVISOR_A_HEADER, ImmutableList.of(clarification2, clarification1));
        clarificationsMap.put(SUPERVISOR_B_HEADER, ImmutableList.of(clarification2, clarification1));
        clarificationsMap.put(CONTESTANT_A_HEADER, ImmutableList.of(clarification1));
        clarificationsMap.put(CONTESTANT_B_HEADER, ImmutableList.of(clarification2));

        Map<AuthHeader, Boolean> canCreateMap = new LinkedHashMap<>();
        canCreateMap.put(ADMIN_HEADER, false);
        canCreateMap.put(MANAGER_HEADER, false);
        canCreateMap.put(SUPERVISOR_A_HEADER, false);
        canCreateMap.put(SUPERVISOR_B_HEADER, false);
        canCreateMap.put(CONTESTANT_A_HEADER, true);
        canCreateMap.put(CONTESTANT_B_HEADER, true);

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
        profilesKeysMap.put(ADMIN_HEADER, ImmutableList.of(CONTESTANT_A_JID, CONTESTANT_B_JID, SUPERVISOR_A_JID));
        profilesKeysMap.put(MANAGER_HEADER, ImmutableList.of(CONTESTANT_A_JID, CONTESTANT_B_JID, SUPERVISOR_A_JID));
        profilesKeysMap.put(
                SUPERVISOR_A_HEADER, ImmutableList.of(CONTESTANT_A_JID, CONTESTANT_B_JID, SUPERVISOR_A_JID));
        profilesKeysMap.put(
                SUPERVISOR_B_HEADER, ImmutableList.of(CONTESTANT_A_JID, CONTESTANT_B_JID, SUPERVISOR_A_JID));
        profilesKeysMap.put(CONTESTANT_A_HEADER, ImmutableList.of(CONTESTANT_A_JID));
        profilesKeysMap.put(CONTESTANT_B_HEADER, ImmutableList.of(CONTESTANT_B_JID, SUPERVISOR_A_JID));

        Map<AuthHeader, Map<String, String>> problemAliasesMapMap = new LinkedHashMap<>();
        problemAliasesMapMap.put(ADMIN_HEADER, ImmutableMap.of(PROBLEM_1_JID, "A"));
        problemAliasesMapMap.put(MANAGER_HEADER, ImmutableMap.of(PROBLEM_1_JID, "A"));
        problemAliasesMapMap.put(SUPERVISOR_A_HEADER, ImmutableMap.of(PROBLEM_1_JID, "A"));
        problemAliasesMapMap.put(SUPERVISOR_B_HEADER, ImmutableMap.of(PROBLEM_1_JID, "A"));
        problemAliasesMapMap.put(CONTESTANT_A_HEADER, ImmutableMap.of(PROBLEM_1_JID, "A", PROBLEM_2_JID, "B"));
        problemAliasesMapMap.put(CONTESTANT_B_HEADER, ImmutableMap.of(PROBLEM_1_JID, "A", PROBLEM_2_JID, "B"));

        Map<AuthHeader, Map<String, String>> problemNamesMapMap = new LinkedHashMap<>();
        problemNamesMapMap.put(ADMIN_HEADER, ImmutableMap.of(PROBLEM_1_JID, "Problem 1"));
        problemNamesMapMap.put(MANAGER_HEADER, ImmutableMap.of(PROBLEM_1_JID, "Problem 1"));
        problemNamesMapMap.put(SUPERVISOR_A_HEADER, ImmutableMap.of(PROBLEM_1_JID, "Problem 1"));
        problemNamesMapMap.put(SUPERVISOR_B_HEADER, ImmutableMap.of(PROBLEM_1_JID, "Problem 1"));
        problemNamesMapMap.put(
                CONTESTANT_A_HEADER, ImmutableMap.of(PROBLEM_1_JID, "Problem 1", PROBLEM_2_JID, "Problem 2"));
        problemNamesMapMap.put(
                CONTESTANT_B_HEADER, ImmutableMap.of(PROBLEM_1_JID, "Problem 1", PROBLEM_2_JID, "Problem 2"));

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
        ContestClarification clarification1 = createClarification(CONTESTANT_A_HEADER, PROBLEM_1_JID);
        ContestClarification clarification2 = createAnsweredClarification(CONTESTANT_B_HEADER, contest.getJid());
        ContestClarification clarification3 = createClarification(CONTESTANT_A_HEADER, contest.getJid());
        ContestClarification clarification4 = createAnsweredClarification(CONTESTANT_B_HEADER, PROBLEM_1_JID);

        Map<ContestClarificationStatus, List<ContestClarification>> clarificationsMap = new LinkedHashMap<>();
        clarificationsMap.put(ASKED, ImmutableList.of(clarification3, clarification1));
        clarificationsMap.put(ANSWERED, ImmutableList.of(clarification4, clarification2));

        for (ContestClarificationStatus status : clarificationsMap.keySet()) {
            ContestClarificationsResponse response = clarificationService
                    .getClarifications(SUPERVISOR_A_HEADER, contest.getJid(), of(status.name()), empty(), empty());

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
                SUPERVISOR_A_HEADER,
                contest.getJid(),
                clarification.getJid(),
                new ContestClarificationAnswerData.Builder()
                        .answer(randomString())
                        .build());
    }
}
