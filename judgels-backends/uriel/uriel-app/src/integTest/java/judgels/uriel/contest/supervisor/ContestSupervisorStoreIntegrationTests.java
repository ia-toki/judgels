package judgels.uriel.contest.supervisor;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.DaggerUrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestHibernateModule;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.api.contest.supervisor.ContestSupervisorData;
import judgels.uriel.api.contest.supervisor.SupervisorPermission;
import judgels.uriel.api.contest.supervisor.SupervisorPermissionType;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestSupervisorModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestSupervisorModel.class})
class ContestSupervisorStoreIntegrationTests {
    private static final String USER_1 = "user1Jid";
    private static final String USER_2 = "user2Jid";

    private ContestStore contestStore;
    private ContestSupervisorStore store;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = DaggerUrielIntegrationTestComponent.builder()
                .urielIntegrationTestHibernateModule(new UrielIntegrationTestHibernateModule(sessionFactory))
                .build();

        contestStore = component.contestStore();
        store = component.contestSupervisorStore();
    }

    @Test
    void can_do_basic_crud() {
        Contest contest = contestStore.createContest(new ContestCreateData.Builder().slug("contest-a").build());
        contestStore.createContest(new ContestCreateData.Builder().slug("contest-b").build());

        ContestSupervisor supervisor1 = store.upsertSupervisor(contest.getJid(), new ContestSupervisorData.Builder()
                .userJid(USER_1)
                .permission(SupervisorPermission.of(ImmutableSet.of(SupervisorPermissionType.SCOREBOARD)))
                .build());
        store.upsertSupervisor(contest.getJid(), new ContestSupervisorData.Builder()
                .userJid(USER_2)
                .permission(SupervisorPermission.all())
                .build());

        assertThat(store.getSupervisor(contest.getJid(), USER_1)).contains(supervisor1);

        // TODO(fushar): move these assertions to service integration tests instead
        assertThat(supervisor1.getUserJid())
                .isEqualTo(USER_1);
        assertThat(supervisor1.getPermission())
                .isEqualTo(SupervisorPermission.of(ImmutableSet.of(SupervisorPermissionType.SCOREBOARD)));
    }
}
