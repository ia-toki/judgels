package judgels.uriel.contest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.DaggerUrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestHibernateModule;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.supervisor.ContestSupervisorData;
import judgels.uriel.api.contest.supervisor.SupervisorPermission;
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
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {
        AdminRoleModel.class,
        ContestModel.class,
        ContestContestantModel.class,
        ContestSupervisorModel.class,
        ContestManagerModel.class,
        ContestModuleModel.class})
class ContestStoreIntegrationTests {
    private static final String ADMIN = "adminJid";
    private static final String USER_1 = "user1Jid";
    private static final String USER_2 = "user2Jid";
    private static final String USER_3 = "user3Jid";

    private ContestStore store;

    private AdminRoleStore adminRoleStore;
    private ContestModuleStore moduleStore;
    private ContestContestantStore contestantStore;
    private ContestSupervisorStore supervisorStore;
    private ContestManagerStore managerStore;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = DaggerUrielIntegrationTestComponent.builder()
                .urielIntegrationTestHibernateModule(new UrielIntegrationTestHibernateModule(sessionFactory))
                .build();

        adminRoleStore = component.adminRoleStore();
        moduleStore = component.contestModuleStore();
        contestantStore = component.contestContestantStore();
        supervisorStore = component.contestSupervisorStore();
        managerStore = component.contestManagerStore();

        store = component.contestStore();
    }

    @Test
    void get_contests() {
        Contest contestA = store.createContest(new ContestData.Builder().slug("contest-a").build());
        Contest contestB = store.createContest(new ContestData.Builder().slug("contest-b").build());
        Contest contestC = store.createContest(new ContestData.Builder().slug("contest-c").build());
        Contest contestD = store.createContest(new ContestData.Builder().slug("contest-d").build());

        adminRoleStore.addAdmin(ADMIN);
        moduleStore.upsertRegistrationModule(contestD.getJid());
        contestantStore.upsertContestant(contestA.getJid(), USER_1);
        contestantStore.upsertContestant(contestA.getJid(), USER_2);
        contestantStore.upsertContestant(contestA.getJid(), USER_3);
        supervisorStore.upsertSupervisor(
                contestB.getJid(),
                new ContestSupervisorData.Builder().userJid(USER_2).permission(SupervisorPermission.all()).build());
        managerStore.upsertManager(contestC.getJid(), USER_3);

        assertThat(getContests(ADMIN)).containsExactly(contestD, contestC, contestB, contestA);
        assertThat(getContests(USER_1)).containsExactly(contestD, contestA);
        assertThat(getContests(USER_2)).containsExactly(contestD, contestB, contestA);
        assertThat(getContests(USER_3)).containsExactly(contestD, contestC, contestA);
    }

    private List<Contest> getContests(String userJid) {
        return store.getContests(userJid, SelectionOptions.DEFAULT_PAGED).getData();
    }
}
