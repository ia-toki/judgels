package judgels.uriel.api.contest.web;

import static java.util.Optional.empty;
import static java.util.Optional.of;
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
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.role.ContestRole;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestWebServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private final ContestWebService webService = createService(ContestWebService.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .supervisorWithManagementPermissions(SUPERVISOR_A, SupervisorManagementPermission.ALL)
                .supervisors(SUPERVISOR_B)
                .modules(ContestModuleType.REGISTRATION)
                .build();
    }

    @Test
    void get_config__role() {
        Map<Optional<AuthHeader>, ContestRole> rolesMap = new LinkedHashMap<>();
        rolesMap.put(of(ADMIN_HEADER), ContestRole.ADMIN);
        rolesMap.put(of(MANAGER_HEADER), ContestRole.MANAGER);
        rolesMap.put(of(SUPERVISOR_A_HEADER), ContestRole.SUPERVISOR);
        rolesMap.put(of(SUPERVISOR_B_HEADER), ContestRole.SUPERVISOR);
        rolesMap.put(of(CONTESTANT_HEADER), ContestRole.CONTESTANT);
        rolesMap.put(of(USER_HEADER), ContestRole.NONE);
        rolesMap.put(empty(), ContestRole.NONE);

        for (Optional<AuthHeader> authHeader : rolesMap.keySet()) {
            assertThat(webService.getWebConfig(authHeader, contest.getJid()).getRole())
                    .isEqualTo(rolesMap.get(authHeader));
        }
    }

    @Test
    void get_config__can_manage() {
        Map<Optional<AuthHeader>, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(of(ADMIN_HEADER), true);
        canManageMap.put(of(MANAGER_HEADER), true);
        canManageMap.put(of(SUPERVISOR_A_HEADER), false);
        canManageMap.put(of(SUPERVISOR_B_HEADER), false);
        canManageMap.put(of(CONTESTANT_HEADER), false);
        canManageMap.put(of(USER_HEADER), false);
        canManageMap.put(empty(), false);

        for (Optional<AuthHeader> authHeader : canManageMap.keySet()) {
            assertThat(webService.getWebConfig(authHeader, contest.getJid()).canManage())
                    .isEqualTo(canManageMap.get(authHeader));
        }
    }

    @Test
    void get_config__visible_tabs__no_modules() {
        endContest(contest);

        Map<Optional<AuthHeader>, Set<ContestTab>> visibleTabsMap = new LinkedHashMap<>();
        visibleTabsMap.put(of(ADMIN_HEADER), ImmutableSet.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                CONTESTANTS,
                SUPERVISORS,
                MANAGERS,
                SUBMISSIONS,
                SCOREBOARD,
                LOGS));
        visibleTabsMap.put(of(MANAGER_HEADER), ImmutableSet.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                CONTESTANTS,
                SUPERVISORS,
                MANAGERS,
                SUBMISSIONS,
                SCOREBOARD,
                LOGS));
        visibleTabsMap.put(of(SUPERVISOR_A_HEADER), ImmutableSet.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                CONTESTANTS,
                SUPERVISORS,
                SUBMISSIONS,
                SCOREBOARD));
        visibleTabsMap.put(of(SUPERVISOR_B_HEADER), ImmutableSet.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                CONTESTANTS,
                SUBMISSIONS,
                SCOREBOARD));
        visibleTabsMap.put(of(CONTESTANT_HEADER), ImmutableSet.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                SUBMISSIONS,
                SCOREBOARD));
        visibleTabsMap.put(of(USER_HEADER), ImmutableSet.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                SCOREBOARD));
        visibleTabsMap.put(empty(), ImmutableSet.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                SCOREBOARD));

        for (Optional<AuthHeader> authHeader : visibleTabsMap.keySet()) {
            assertThat(webService.getWebConfig(authHeader, contest.getJid()).getVisibleTabs())
                    .isEqualTo(visibleTabsMap.get(authHeader));
        }
    }

    @Test
    void get_config__visible_tabs__with_modules() {
        enableModule(contest, ContestModuleType.EDITORIAL);
        enableModule(contest, ContestModuleType.CLARIFICATION);
        enableModule(contest, ContestModuleType.FILE);

        endContest(contest);

        Map<Optional<AuthHeader>, Set<ContestTab>> visibleTabsMap = new LinkedHashMap<>();
        visibleTabsMap.put(of(ADMIN_HEADER), ImmutableSet.of(
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
        visibleTabsMap.put(of(MANAGER_HEADER), ImmutableSet.of(
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
        visibleTabsMap.put(of(SUPERVISOR_A_HEADER), ImmutableSet.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                EDITORIAL,
                CONTESTANTS,
                SUPERVISORS,
                SUBMISSIONS,
                CLARIFICATIONS,
                FILES,
                SCOREBOARD));
        visibleTabsMap.put(of(SUPERVISOR_B_HEADER), ImmutableSet.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                EDITORIAL,
                CONTESTANTS,
                SUBMISSIONS,
                CLARIFICATIONS,
                FILES,
                SCOREBOARD));
        visibleTabsMap.put(of(CONTESTANT_HEADER), ImmutableSet.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                EDITORIAL,
                SUBMISSIONS,
                CLARIFICATIONS,
                SCOREBOARD));
        visibleTabsMap.put(of(USER_HEADER), ImmutableSet.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                EDITORIAL,
                SCOREBOARD));
        visibleTabsMap.put(empty(), ImmutableSet.of(
                ANNOUNCEMENTS,
                PROBLEMS,
                EDITORIAL,
                SCOREBOARD));

        for (Optional<AuthHeader> authHeader : visibleTabsMap.keySet()) {
            assertThat(webService.getWebConfig(authHeader, contest.getJid()).getVisibleTabs())
                    .isEqualTo(visibleTabsMap.get(authHeader));
        }
    }

    @Test
    void get_web_config__state() {
        assertThat(webService.getWebConfig(of(CONTESTANT_HEADER), contest.getJid()).getState())
                .isEqualTo(ContestState.NOT_BEGUN);

        beginContest(contest);
        assertThat(webService.getWebConfig(of(CONTESTANT_HEADER), contest.getJid()).getState())
                .isEqualTo(ContestState.STARTED);

        enableModule(contest, ContestModuleType.PAUSE);
        assertThat(webService.getWebConfig(of(CONTESTANT_HEADER), contest.getJid()).getState())
                .isEqualTo(ContestState.PAUSED);
        disableModule(contest, ContestModuleType.PAUSE);

        endContest(contest);
        assertThat(webService.getWebConfig(of(CONTESTANT_HEADER), contest.getJid()).getState())
                .isEqualTo(ContestState.FINISHED);
    }

    @Test
    void get_web_config__state__virtual() {
        enableModule(contest, ContestModuleType.VIRTUAL);

        assertThat(webService.getWebConfig(of(CONTESTANT_HEADER), contest.getJid()).getState())
                .isEqualTo(ContestState.NOT_BEGUN);

        beginContest(contest);
        assertThat(webService.getWebConfig(of(CONTESTANT_HEADER), contest.getJid()).getState())
                .isEqualTo(ContestState.BEGUN);

        contestService.startVirtualContest(CONTESTANT_HEADER, contest.getJid());
        assertThat(webService.getWebConfig(of(CONTESTANT_HEADER), contest.getJid()).getState())
                .isEqualTo(ContestState.STARTED);

        endContest(contest);
        assertThat(webService.getWebConfig(of(CONTESTANT_HEADER), contest.getJid()).getState())
                .isEqualTo(ContestState.FINISHED);
    }
}
