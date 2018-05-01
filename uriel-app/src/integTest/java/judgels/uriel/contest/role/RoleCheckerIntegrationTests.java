package judgels.uriel.contest.role;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.DaggerUrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestHibernateModule;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.supervisor.ContestSupervisorData;
import judgels.uriel.api.contest.supervisor.SupervisorPermission;
import judgels.uriel.api.contest.supervisor.SupervisorPermissionType;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.manager.ContestManagerStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModuleModel;
import judgels.uriel.persistence.ContestSupervisorModel;
import judgels.uriel.role.AdminRoleStore;
import judgels.uriel.role.RoleChecker;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {
        AdminRoleModel.class,
        ContestModel.class,
        ContestModuleModel.class,
        ContestContestantModel.class,
        ContestSupervisorModel.class,
        ContestManagerModel.class})
class RoleCheckerIntegrationTests {
    private static final String USER = "userJid";
    private static final String ADMIN = "adminJid";
    private static final String CONTESTANT = "contestantJid";
    private static final String SUPERVISOR = "supervisorJid";
    private static final String MANAGER = "managerJid";

    private String contestA;
    private String contestB;
    private String contestC;

    private ContestSupervisorStore supervisorStore;

    private RoleChecker roleChecker;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = DaggerUrielIntegrationTestComponent.builder()
                .urielIntegrationTestHibernateModule(new UrielIntegrationTestHibernateModule(sessionFactory))
                .build();

        AdminRoleStore adminRoleStore = component.adminRoleStore();
        ContestStore contestStore = component.contestStore();
        ContestModuleStore moduleStore = component.contestModuleStore();
        ContestContestantStore contestantStore = component.contestContestantStore();
        supervisorStore = component.contestSupervisorStore();
        ContestManagerStore managerStore = component.contestManagerStore();

        roleChecker = component.roleChecker();

        adminRoleStore.addAdmin(ADMIN);

        contestA = contestStore.createContest(new ContestData.Builder().name("Contest A").build()).getJid();
        contestB = contestStore.createContest(new ContestData.Builder().name("Contest B").build()).getJid();
        contestC = contestStore.createContest(new ContestData.Builder().name("Contest C").build()).getJid();

        moduleStore.upsertRegistrationModule(contestA);
        contestantStore.upsertContestant(contestB, CONTESTANT);
        supervisorStore.upsertSupervisor(
                contestB,
                new ContestSupervisorData.Builder()
                        .userJid(SUPERVISOR)
                        .permission(SupervisorPermission.of(ImmutableSet.of())).build());
        managerStore.upsertManager(contestB, MANAGER);
    }

    @Test
    void create_contest() {
        assertThat(roleChecker.canCreateContest(ADMIN)).isTrue();
        assertThat(roleChecker.canCreateContest(USER)).isFalse();
        assertThat(roleChecker.canCreateContest(CONTESTANT)).isFalse();
        assertThat(roleChecker.canCreateContest(SUPERVISOR)).isFalse();
        assertThat(roleChecker.canCreateContest(MANAGER)).isFalse();
    }

    @Test
    void view_contest() {
        assertThat(roleChecker.canViewContest(ADMIN, contestA)).isTrue();
        assertThat(roleChecker.canViewContest(ADMIN, contestB)).isTrue();
        assertThat(roleChecker.canViewContest(ADMIN, contestC)).isTrue();

        assertThat(roleChecker.canViewContest(USER, contestA)).isTrue();
        assertThat(roleChecker.canViewContest(USER, contestB)).isFalse();
        assertThat(roleChecker.canViewContest(USER, contestC)).isFalse();

        assertThat(roleChecker.canViewContest(CONTESTANT, contestA)).isTrue();
        assertThat(roleChecker.canViewContest(CONTESTANT, contestB)).isTrue();
        assertThat(roleChecker.canViewContest(CONTESTANT, contestC)).isFalse();

        assertThat(roleChecker.canViewContest(SUPERVISOR, contestA)).isTrue();
        assertThat(roleChecker.canViewContest(SUPERVISOR, contestB)).isTrue();
        assertThat(roleChecker.canViewContest(SUPERVISOR, contestC)).isFalse();

        assertThat(roleChecker.canViewContest(MANAGER, contestA)).isTrue();
        assertThat(roleChecker.canViewContest(MANAGER, contestB)).isTrue();
        assertThat(roleChecker.canViewContest(MANAGER, contestC)).isFalse();
    }

    @Test
    void view_scoreboard() {
        assertThat(roleChecker.canViewScoreboard(ADMIN, contestA)).isTrue();
        assertThat(roleChecker.canViewScoreboard(ADMIN, contestB)).isTrue();
        assertThat(roleChecker.canViewScoreboard(ADMIN, contestC)).isTrue();

        assertThat(roleChecker.canViewScoreboard(USER, contestA)).isTrue();
        assertThat(roleChecker.canViewScoreboard(USER, contestB)).isFalse();
        assertThat(roleChecker.canViewScoreboard(USER, contestC)).isFalse();

        assertThat(roleChecker.canViewScoreboard(CONTESTANT, contestA)).isTrue();
        assertThat(roleChecker.canViewScoreboard(CONTESTANT, contestB)).isTrue();
        assertThat(roleChecker.canViewScoreboard(CONTESTANT, contestC)).isFalse();

        assertThat(roleChecker.canViewScoreboard(SUPERVISOR, contestA)).isTrue();
        assertThat(roleChecker.canViewScoreboard(SUPERVISOR, contestB)).isTrue();
        assertThat(roleChecker.canViewScoreboard(SUPERVISOR, contestC)).isFalse();

        assertThat(roleChecker.canViewScoreboard(MANAGER, contestA)).isTrue();
        assertThat(roleChecker.canViewScoreboard(MANAGER, contestB)).isTrue();
        assertThat(roleChecker.canViewScoreboard(MANAGER, contestC)).isFalse();
    }

    @Test
    void supervise_scoreboard() {
        assertThat(roleChecker.canSuperviseScoreboard(ADMIN, contestA)).isTrue();
        assertThat(roleChecker.canSuperviseScoreboard(ADMIN, contestB)).isTrue();
        assertThat(roleChecker.canSuperviseScoreboard(ADMIN, contestC)).isTrue();

        assertThat(roleChecker.canSuperviseScoreboard(USER, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(USER, contestB)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(USER, contestC)).isFalse();

        assertThat(roleChecker.canSuperviseScoreboard(CONTESTANT, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(CONTESTANT, contestB)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(CONTESTANT, contestC)).isFalse();


        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestB)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(SupervisorPermissionType.SCOREBOARD);
        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestB)).isTrue();
        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestC)).isFalse();

        assertThat(roleChecker.canSuperviseScoreboard(MANAGER, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(MANAGER, contestB)).isTrue();
        assertThat(roleChecker.canSuperviseScoreboard(MANAGER, contestC)).isFalse();
    }

    @Test
    void add_contestants() {
        assertThat(roleChecker.canAddContestants(ADMIN, contestA)).isTrue();
        assertThat(roleChecker.canAddContestants(ADMIN, contestB)).isTrue();
        assertThat(roleChecker.canAddContestants(ADMIN, contestC)).isTrue();

        assertThat(roleChecker.canAddContestants(USER, contestA)).isFalse();
        assertThat(roleChecker.canAddContestants(USER, contestB)).isFalse();
        assertThat(roleChecker.canAddContestants(USER, contestC)).isFalse();

        assertThat(roleChecker.canAddContestants(CONTESTANT, contestA)).isFalse();
        assertThat(roleChecker.canAddContestants(CONTESTANT, contestB)).isFalse();
        assertThat(roleChecker.canAddContestants(CONTESTANT, contestC)).isFalse();

        assertThat(roleChecker.canAddContestants(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canAddContestants(SUPERVISOR, contestB)).isFalse();
        assertThat(roleChecker.canAddContestants(SUPERVISOR, contestC)).isFalse();

        assertThat(roleChecker.canAddContestants(MANAGER, contestA)).isFalse();
        assertThat(roleChecker.canAddContestants(MANAGER, contestB)).isTrue();
        assertThat(roleChecker.canAddContestants(MANAGER, contestC)).isFalse();
    }

    private void addSupervisorToContestBWithPermission(SupervisorPermissionType type) {
        supervisorStore.upsertSupervisor(
                contestB,
                new ContestSupervisorData.Builder()
                        .userJid(SUPERVISOR)
                        .permission(SupervisorPermission.of(ImmutableSet.of(type))).build());
    }
}
