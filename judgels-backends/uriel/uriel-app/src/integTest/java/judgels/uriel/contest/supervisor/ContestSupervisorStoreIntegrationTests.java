package judgels.uriel.contest.supervisor;

import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.ALL;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.FILE;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.SCOREBOARD;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import judgels.persistence.api.Page;
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

        assertThat(store.isSupervisorWithManagementPermission(contest.getJid(), "userJidA", SCOREBOARD)).isFalse();

        ContestSupervisor supervisorA =
                store.upsertSupervisor(contest.getJid(), "userJidA", ImmutableSet.of(SCOREBOARD));
        store.upsertSupervisor(contest.getJid(), "userJidB", ImmutableSet.of(ALL));

        assertThat(store.getSupervisor(contest.getJid(), "userJidA")).contains(supervisorA);
        assertThat(store.isSupervisorWithManagementPermission(contest.getJid(), "userJidA", SCOREBOARD)).isTrue();

        assertThat(store.isSupervisorWithManagementPermission(contest.getJid(), "userJidA", FILE)).isFalse();
        store.upsertSupervisor(contest.getJid(), "userJidA", ImmutableSet.of(FILE, SCOREBOARD));
        assertThat(store.isSupervisorWithManagementPermission(contest.getJid(), "userJidA", FILE)).isTrue();

        Page<ContestSupervisor> supervisors = store.getSupervisors(contest.getJid(), Optional.empty());
        assertThat(supervisors.getPage()).containsOnly(
                new ContestSupervisor.Builder().userJid("userJidA").addManagementPermissions(FILE, SCOREBOARD).build(),
                new ContestSupervisor.Builder().userJid("userJidB").addManagementPermissions(ALL).build());

        assertThat(store.deleteSupervisor(contest.getJid(), "userJidA")).isTrue();
        assertThat(store.deleteSupervisor(contest.getJid(), "userJidC")).isFalse();

        supervisors = store.getSupervisors(contest.getJid(), Optional.empty());
        assertThat(supervisors.getPage()).containsOnly(
                new ContestSupervisor.Builder().userJid("userJidB").addManagementPermissions(ALL).build());
    }
}
