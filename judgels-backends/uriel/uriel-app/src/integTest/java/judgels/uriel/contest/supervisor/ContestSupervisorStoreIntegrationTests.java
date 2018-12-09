package judgels.uriel.contest.supervisor;

import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.ALL;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.FILE;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.SCOREBOARD;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestSupervisorModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestSupervisorModel.class})
class ContestSupervisorStoreIntegrationTests extends AbstractIntegrationTests {
    private static final String USER_1 = "user1Jid";
    private static final String USER_2 = "user2Jid";

    private ContestStore contestStore;
    private ContestSupervisorStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        contestStore = component.contestStore();
        store = component.contestSupervisorStore();
    }

    @Test
    void crud_flow() {
        Contest contest = contestStore.createContest(new ContestCreateData.Builder().slug("contest-a").build());
        contestStore.createContest(new ContestCreateData.Builder().slug("contest-b").build());

        assertThat(store.isSupervisorWithManagementPermission(contest.getJid(), USER_1, SCOREBOARD)).isFalse();

        ContestSupervisor supervisor1 = store.upsertSupervisor(contest.getJid(), USER_1, ImmutableSet.of(SCOREBOARD));
        store.upsertSupervisor(contest.getJid(), USER_2, ImmutableSet.of(ALL));

        assertThat(store.getSupervisor(contest.getJid(), USER_1)).contains(supervisor1);
        assertThat(store.isSupervisorWithManagementPermission(contest.getJid(), USER_1, SCOREBOARD)).isTrue();

        assertThat(store.isSupervisorWithManagementPermission(contest.getJid(), USER_1, FILE)).isFalse();
        store.upsertSupervisor(contest.getJid(), USER_1, ImmutableSet.of(FILE, SCOREBOARD));
        assertThat(store.isSupervisorWithManagementPermission(contest.getJid(), USER_1, FILE)).isTrue();

        // TODO(fushar): move these assertions to service integration tests instead
        assertThat(supervisor1.getUserJid()).isEqualTo(USER_1);
        assertThat(supervisor1.getManagementPermissions()).isEqualTo(ImmutableSet.of(SCOREBOARD));
    }
}
