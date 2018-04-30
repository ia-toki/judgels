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
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.manager.ContestManagerStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.supervisor.ContestSupervisor;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.contest.supervisor.SupervisorPermission;
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
        Contest contestA = store.createContest(new ContestData.Builder().name("contestA").build());
        Contest contestB = store.createContest(new ContestData.Builder().name("contestB").build());
        Contest contestC = store.createContest(new ContestData.Builder().name("contestC").build());
        Contest contestD = store.createContest(new ContestData.Builder().name("contestD").build());

        adminRoleStore.addAdmin(ADMIN);
        moduleStore.upsertModule(contestD.getJid(), ContestModuleType.REGISTRATION);
        contestantStore.upsertContestant(contestA.getJid(), USER_1);
        contestantStore.upsertContestant(contestA.getJid(), USER_2);
        contestantStore.upsertContestant(contestA.getJid(), USER_3);
        supervisorStore.upsertSupervisor(
                contestB.getJid(),
                new ContestSupervisor.Builder().userJid(USER_2).permission(SupervisorPermission.all()).build());
        managerStore.upsertManager(contestC.getJid(), USER_3);

        assertThat(getContests(ADMIN)).containsExactly(contestA, contestB, contestC, contestD);
        assertThat(getContests(USER_1)).containsExactly(contestA, contestD);
        assertThat(getContests(USER_2)).containsExactly(contestA, contestB, contestD);
        assertThat(getContests(USER_3)).containsExactly(contestA, contestC, contestD);
    }

    private List<Contest> getContests(String userJid) {
        return store.getContests(userJid, SelectionOptions.DEFAULT).getData();
    }
}
