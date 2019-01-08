package judgels.uriel.contest;

import static judgels.uriel.api.contest.ContestErrors.SLUG_ALREADY_EXISTS;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.ALL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableSet;
import com.palantir.conjure.java.api.errors.ServiceException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestUpdateData;
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
class ContestStoreIntegrationTests extends AbstractIntegrationTests {
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
    void setUpSession(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        adminRoleStore = component.adminRoleStore();
        moduleStore = component.contestModuleStore();
        contestantStore = component.contestContestantStore();
        supervisorStore = component.contestSupervisorStore();
        managerStore = component.contestManagerStore();

        store = component.contestStore();
    }

    @Test
    void crud_flow() {
        Contest contestA = store.createContest(new ContestCreateData.Builder().slug("contest-a").build());
        contestA = store.updateContest(contestA.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.ofEpochSecond(2)).build()).get();

        Contest contestB = store.createContest(new ContestCreateData.Builder().slug("contest-b").build());
        contestB = store.updateContest(contestB.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.ofEpochSecond(1)).build()).get();

        Contest contestC = store.createContest(new ContestCreateData.Builder().slug("contest-c").build());
        contestC = store.updateContest(contestC.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.ofEpochSecond(3)).build()).get();

        Contest contestD = store.createContest(new ContestCreateData.Builder().slug("contest-d").build());
        contestD = store.updateContest(contestD.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.ofEpochSecond(4)).build()).get();

        assertThatThrownBy(() -> store.createContest(new ContestCreateData.Builder().slug("contest-d").build()))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(SLUG_ALREADY_EXISTS.name());

        adminRoleStore.upsertAdmin(ADMIN);
        moduleStore.upsertRegistrationModule(contestD.getJid());
        contestantStore.upsertContestant(contestA.getJid(), USER_1);
        contestantStore.upsertContestant(contestA.getJid(), USER_2);
        contestantStore.upsertContestant(contestA.getJid(), USER_3);
        supervisorStore.upsertSupervisor(contestB.getJid(), USER_2, ImmutableSet.of(ALL));
        managerStore.upsertManager(contestC.getJid(), USER_3);

        assertThat(getContests(ADMIN)).containsExactly(contestD, contestC, contestA, contestB);
        assertThat(getContests(USER_1)).containsExactly(contestD, contestA);
        assertThat(getContests(USER_2)).containsExactly(contestD, contestA, contestB);
        assertThat(getContests(USER_3)).containsExactly(contestD, contestC, contestA);
    }

    @Test
    void throws_on_duplicate_slugs() {
        Contest contestA = store.createContest(new ContestCreateData.Builder().slug("contest-a").build());
        store.createContest(new ContestCreateData.Builder().slug("contest-b").build());

        assertThatThrownBy(() -> store.createContest(new ContestCreateData.Builder().slug("contest-a").build()))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(SLUG_ALREADY_EXISTS.name());

        store.updateContest(contestA.getJid(), new ContestUpdateData.Builder().name("Contest A").build());

        assertThatThrownBy(() -> store.updateContest(
                contestA.getJid(),
                new ContestUpdateData.Builder().slug("contest-b").build()))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(SLUG_ALREADY_EXISTS.name());
    }

    private List<Contest> getContests(String userJid) {
        return store.getContests(userJid, Optional.empty(), Optional.empty()).getPage();
    }
}
