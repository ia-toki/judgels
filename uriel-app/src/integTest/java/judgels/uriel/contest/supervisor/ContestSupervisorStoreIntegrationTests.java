package judgels.uriel.contest.supervisor;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.DaggerUrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestHibernateModule;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestSupervisorModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestSupervisorModel.class})
class ContestSupervisorStoreIntegrationTests {
    private static final String USER1 = "user1Jid";
    private static final String USER2 = "user2Jid";

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
        Contest contest = contestStore.createContest(new ContestData.Builder().name("contestA").build());

        ContestSupervisor supervisor1 = store.upsertSupervisor(contest.getJid(), new ContestSupervisor.Builder()
                .userJid(USER1)
                .permission(SupervisorPermission.of(ImmutableSet.of(SupervisorPermissionType.SCOREBOARD)))
                .build());
        store.upsertSupervisor(contest.getJid(), new ContestSupervisor.Builder()
                .userJid(USER2)
                .permission(SupervisorPermission.all())
                .build());

        assertThat(store.findSupervisor(contest.getJid(), USER1)).contains(supervisor1);

        assertThat(supervisor1.getUserJid())
                .isEqualTo(USER1);
        assertThat(supervisor1.getPermission())
                .isEqualTo(SupervisorPermission.of(ImmutableSet.of(SupervisorPermissionType.SCOREBOARD)));
    }
}
