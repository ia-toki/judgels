package judgels.uriel.api;

import static judgels.uriel.api.contest.web.ContestTab.ANNOUNCEMENTS;
import static judgels.uriel.api.contest.web.ContestTab.CLARIFICATIONS;
import static judgels.uriel.api.contest.web.ContestTab.CONTESTANTS;
import static judgels.uriel.api.contest.web.ContestTab.EDITORIAL;
import static judgels.uriel.api.contest.web.ContestTab.FILES;
import static judgels.uriel.api.contest.web.ContestTab.LOGS;
import static judgels.uriel.api.contest.web.ContestTab.MANAGERS;
import static judgels.uriel.api.contest.web.ContestTab.PROBLEMS;
import static judgels.uriel.api.contest.web.ContestTab.SCOREBOARD;
import static judgels.uriel.api.contest.web.ContestTab.SUBMISSIONS;
import static judgels.uriel.api.contest.web.ContestTab.SUPERVISORS;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import judgels.uriel.ContestClarificationClient;
import judgels.uriel.ContestWebClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.clarification.ContestClarification;
import judgels.uriel.api.contest.clarification.ContestClarificationAnswerData;
import judgels.uriel.api.contest.clarification.ContestClarificationData;
import judgels.uriel.api.contest.clarification.ContestClarificationStatus;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.role.ContestRole;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import judgels.uriel.api.contest.web.ContestState;
import judgels.uriel.api.contest.web.ContestTab;
import judgels.uriel.api.contest.web.ContestWebConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestWebApiIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestWebClient webClient = createClient(ContestWebClient.class);
    private final ContestClarificationClient clarificationClient = createClient(ContestClarificationClient.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .contestants(CONTESTANT, CONTESTANT_A, CONTESTANT_B)
                .supervisorWithManagementPermissions(SUPERVISOR_A, SupervisorManagementPermission.ALL)
                .supervisors(SUPERVISOR_B)
                .modules(ContestModuleType.REGISTRATION)
                .build();
    }

    @Test
    void get_config__role() {
        Map<String, ContestRole> rolesMap = new LinkedHashMap<>();
        rolesMap.put(adminToken, ContestRole.ADMIN);
        rolesMap.put(managerToken, ContestRole.MANAGER);
        rolesMap.put(supervisorAToken, ContestRole.SUPERVISOR);
        rolesMap.put(supervisorBToken, ContestRole.SUPERVISOR);
        rolesMap.put(contestantToken, ContestRole.CONTESTANT);
        rolesMap.put(userToken, ContestRole.NONE);
        rolesMap.put("", ContestRole.NONE);

        for (String authHeader : rolesMap.keySet()) {
            assertThat(webClient.getWebConfig(authHeader, contest.getJid()).getRole())
                    .isEqualTo(rolesMap.get(authHeader));
        }
    }

    @Test
    void get_config__can_manage() {
        Map<String, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(adminToken, true);
        canManageMap.put(managerToken, true);
        canManageMap.put(supervisorAToken, false);
        canManageMap.put(supervisorBToken, false);
        canManageMap.put(contestantToken, false);
        canManageMap.put(userToken, false);
        canManageMap.put("", false);

        for (String authHeader : canManageMap.keySet()) {
            assertThat(webClient.getWebConfig(authHeader, contest.getJid()).canManage())
                    .isEqualTo(canManageMap.get(authHeader));
        }
    }

    @Test
    void get_config__visible_tabs__no_modules() {
        endContest(contest);

        Map<String, Set<ContestTab>> visibleTabsMap = new LinkedHashMap<>();
        visibleTabsMap.put(adminToken, Set.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                CONTESTANTS,
                SUPERVISORS,
                MANAGERS,
                SUBMISSIONS,
                SCOREBOARD,
                LOGS));
        visibleTabsMap.put(managerToken, Set.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                CONTESTANTS,
                SUPERVISORS,
                MANAGERS,
                SUBMISSIONS,
                SCOREBOARD,
                LOGS));
        visibleTabsMap.put(supervisorAToken, Set.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                CONTESTANTS,
                SUPERVISORS,
                SUBMISSIONS,
                SCOREBOARD));
        visibleTabsMap.put(supervisorBToken, Set.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                CONTESTANTS,
                SUBMISSIONS,
                SCOREBOARD));
        visibleTabsMap.put(contestantToken, Set.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                SUBMISSIONS,
                SCOREBOARD));
        visibleTabsMap.put(userToken, Set.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                SCOREBOARD));
        visibleTabsMap.put("", Set.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                SCOREBOARD));

        for (String authHeader : visibleTabsMap.keySet()) {
            assertThat(webClient.getWebConfig(authHeader, contest.getJid()).getVisibleTabs())
                    .isEqualTo(visibleTabsMap.get(authHeader));
        }
    }

    @Test
    void get_config__visible_tabs__with_modules() {
        enableModule(contest, ContestModuleType.EDITORIAL);
        enableModule(contest, ContestModuleType.CLARIFICATION);
        enableModule(contest, ContestModuleType.FILE);

        endContest(contest);

        Map<String, Set<ContestTab>> visibleTabsMap = new LinkedHashMap<>();
        visibleTabsMap.put(adminToken, Set.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                EDITORIAL,
                CONTESTANTS,
                SUPERVISORS,
                MANAGERS,
                SUBMISSIONS,
                CLARIFICATIONS,
                SCOREBOARD,
                FILES,
                LOGS));
        visibleTabsMap.put(managerToken, Set.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                EDITORIAL,
                CONTESTANTS,
                SUPERVISORS,
                MANAGERS,
                SUBMISSIONS,
                CLARIFICATIONS,
                SCOREBOARD,
                FILES,
                LOGS));
        visibleTabsMap.put(supervisorAToken, Set.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                EDITORIAL,
                CONTESTANTS,
                SUPERVISORS,
                SUBMISSIONS,
                CLARIFICATIONS,
                FILES,
                SCOREBOARD));
        visibleTabsMap.put(supervisorBToken, Set.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                EDITORIAL,
                CONTESTANTS,
                SUBMISSIONS,
                CLARIFICATIONS,
                FILES,
                SCOREBOARD));
        visibleTabsMap.put(contestantToken, Set.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                EDITORIAL,
                SUBMISSIONS,
                CLARIFICATIONS,
                SCOREBOARD));
        visibleTabsMap.put(userToken, Set.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                EDITORIAL,
                SCOREBOARD));
        visibleTabsMap.put("", Set.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                EDITORIAL,
                SCOREBOARD));

        for (String authHeader : visibleTabsMap.keySet()) {
            assertThat(webClient.getWebConfig(authHeader, contest.getJid()).getVisibleTabs())
                    .isEqualTo(visibleTabsMap.get(authHeader));
        }
    }

    @Test
    void get_web_config__state() {
        assertThat(webClient.getWebConfig(contestantToken, contest.getJid()).getState())
                .isEqualTo(ContestState.NOT_BEGUN);

        beginContest(contest);
        assertThat(webClient.getWebConfig(contestantToken, contest.getJid()).getState())
                .isEqualTo(ContestState.STARTED);

        enableModule(contest, ContestModuleType.PAUSE);
        assertThat(webClient.getWebConfig(contestantToken, contest.getJid()).getState())
                .isEqualTo(ContestState.PAUSED);
        disableModule(contest, ContestModuleType.PAUSE);

        endContest(contest);
        assertThat(webClient.getWebConfig(contestantToken, contest.getJid()).getState())
                .isEqualTo(ContestState.FINISHED);
    }

    @Test
    void get_web_config__state__virtual() {
        enableModule(contest, ContestModuleType.VIRTUAL);

        assertThat(webClient.getWebConfig(contestantToken, contest.getJid()).getState())
                .isEqualTo(ContestState.NOT_BEGUN);

        beginContest(contest);
        assertThat(webClient.getWebConfig(contestantToken, contest.getJid()).getState())
                .isEqualTo(ContestState.BEGUN);

        contestClient.startVirtualContest(contestantToken, contest.getJid());
        assertThat(webClient.getWebConfig(contestantToken, contest.getJid()).getState())
                .isEqualTo(ContestState.STARTED);

        endContest(contest);
        assertThat(webClient.getWebConfig(contestantToken, contest.getJid()).getState())
                .isEqualTo(ContestState.FINISHED);
    }

    @Test
    void get_web_config__clarifications() {
        enableModule(contest, ContestModuleType.CLARIFICATION);
        beginContest(contest);

        ContestWebConfig config = webClient.getWebConfig(contestantAToken, contest.getJid());
        assertThat(config.getClarificationStatus()).isEqualTo(ContestClarificationStatus.ANSWERED);
        assertThat(config.getClarificationCount()).isEqualTo(0);

        config = webClient.getWebConfig(contestantBToken, contest.getJid());
        assertThat(config.getClarificationStatus()).isEqualTo(ContestClarificationStatus.ANSWERED);
        assertThat(config.getClarificationCount()).isEqualTo(0);

        config = webClient.getWebConfig(supervisorAToken, contest.getJid());
        assertThat(config.getClarificationStatus()).isEqualTo(ContestClarificationStatus.ASKED);
        assertThat(config.getClarificationCount()).isEqualTo(0);

        ContestClarificationData data = new ContestClarificationData.Builder()
                .topicJid(contest.getJid())
                .title("title")
                .question("question")
                .build();

        ContestClarification c1 = clarificationClient.createClarification(contestantAToken, contest.getJid(), data);
        clarificationClient.createClarification(contestantAToken, contest.getJid(), data);
        clarificationClient.createClarification(contestantBToken, contest.getJid(), data);

        assertThat(webClient.getWebConfig(contestantAToken, contest.getJid()).getClarificationCount())
                .isEqualTo(0);
        assertThat(webClient.getWebConfig(contestantBToken, contest.getJid()).getClarificationCount())
                .isEqualTo(0);
        assertThat(webClient.getWebConfig(supervisorAToken, contest.getJid()).getClarificationCount())
                .isEqualTo(3);

        ContestClarificationAnswerData answerData = new ContestClarificationAnswerData.Builder()
                .answer("answer")
                .build();

        clarificationClient.answerClarification(managerToken, contest.getJid(), c1.getJid(), answerData);

        assertThat(webClient.getWebConfig(contestantAToken, contest.getJid()).getClarificationCount())
                .isEqualTo(1);
        assertThat(webClient.getWebConfig(contestantBToken, contest.getJid()).getClarificationCount())
                .isEqualTo(0);
        assertThat(webClient.getWebConfig(supervisorAToken, contest.getJid()).getClarificationCount())
                .isEqualTo(2);
    }
}
