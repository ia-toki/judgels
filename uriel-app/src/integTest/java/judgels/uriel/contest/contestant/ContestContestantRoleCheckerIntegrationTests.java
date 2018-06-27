package judgels.uriel.contest.contestant;

import static java.time.temporal.ChronoUnit.HOURS;
import static judgels.uriel.UrielIntegrationTestPersistenceModule.NOW;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.supervisor.SupervisorPermissionType;
import judgels.uriel.contest.role.AbstractRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestContestantRoleCheckerIntegrationTests extends AbstractRoleCheckerIntegrationTests {
    private ContestContestantRoleChecker checker;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestContestantRoleChecker();
    }

    @Test
    void register() {
        Contest contestAFinished = contestStore.createContest(new ContestData.Builder()
                .name("Contest A - Ended")
                .beginTime(NOW.minus(10, HOURS))
                .build());
        moduleStore.upsertRegistrationModule(contestAFinished.getJid());

        assertThat(checker.canRegister(ADMIN, contestA)).isTrue();
        assertThat(checker.canRegister(ADMIN, contestAStarted)).isTrue();
        assertThat(checker.canRegister(ADMIN, contestAFinished)).isFalse();
        assertThat(checker.canRegister(ADMIN, contestB)).isFalse();
        assertThat(checker.canRegister(ADMIN, contestBStarted)).isFalse();
        assertThat(checker.canRegister(ADMIN, contestC)).isFalse();

        assertThat(checker.canRegister(USER, contestA)).isTrue();
        assertThat(checker.canRegister(USER, contestAStarted)).isTrue();
        assertThat(checker.canRegister(USER, contestAFinished)).isFalse();
        assertThat(checker.canRegister(USER, contestB)).isFalse();
        assertThat(checker.canRegister(USER, contestBStarted)).isFalse();
        assertThat(checker.canRegister(USER, contestC)).isFalse();

        assertThat(checker.canRegister(CONTESTANT, contestA)).isTrue();
        assertThat(checker.canRegister(CONTESTANT, contestAStarted)).isTrue();
        assertThat(checker.canRegister(CONTESTANT, contestAFinished)).isFalse();
        assertThat(checker.canRegister(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canRegister(CONTESTANT, contestBStarted)).isFalse();
        assertThat(checker.canRegister(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canRegister(SUPERVISOR, contestA)).isTrue();
        assertThat(checker.canRegister(SUPERVISOR, contestAStarted)).isTrue();
        assertThat(checker.canRegister(SUPERVISOR, contestAFinished)).isFalse();
        assertThat(checker.canRegister(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canRegister(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(checker.canRegister(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canRegister(MANAGER, contestA)).isTrue();
        assertThat(checker.canRegister(MANAGER, contestAStarted)).isTrue();
        assertThat(checker.canRegister(MANAGER, contestAFinished)).isFalse();
        assertThat(checker.canRegister(MANAGER, contestB)).isFalse();
        assertThat(checker.canRegister(MANAGER, contestBStarted)).isFalse();
        assertThat(checker.canRegister(MANAGER, contestC)).isFalse();
    }

    @Test
    void unregister() {
        assertThat(checker.canUnregister(ADMIN, contestA)).isFalse();
        assertThat(checker.canUnregister(ADMIN, contestAStarted)).isFalse();
        assertThat(checker.canUnregister(ADMIN, contestB)).isFalse();
        assertThat(checker.canUnregister(ADMIN, contestBStarted)).isFalse();
        assertThat(checker.canUnregister(ADMIN, contestC)).isFalse();

        assertThat(checker.canUnregister(USER, contestA)).isFalse();
        assertThat(checker.canUnregister(USER, contestAStarted)).isFalse();
        assertThat(checker.canUnregister(USER, contestB)).isFalse();
        assertThat(checker.canUnregister(USER, contestBStarted)).isFalse();
        assertThat(checker.canUnregister(USER, contestC)).isFalse();

        assertThat(checker.canUnregister(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canUnregister(CONTESTANT, contestAStarted)).isFalse();
        assertThat(checker.canUnregister(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canUnregister(CONTESTANT, contestBStarted)).isFalse();
        moduleStore.upsertRegistrationModule(contestB.getJid());
        assertThat(checker.canUnregister(CONTESTANT, contestB)).isTrue();
        assertThat(checker.canUnregister(CONTESTANT, contestBStarted)).isFalse();
        assertThat(checker.canUnregister(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canUnregister(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canUnregister(SUPERVISOR, contestAStarted)).isFalse();
        assertThat(checker.canUnregister(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canUnregister(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(checker.canUnregister(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canUnregister(MANAGER, contestA)).isFalse();
        assertThat(checker.canUnregister(MANAGER, contestAStarted)).isFalse();
        assertThat(checker.canUnregister(MANAGER, contestB)).isFalse();
        assertThat(checker.canUnregister(MANAGER, contestBStarted)).isFalse();
        assertThat(checker.canUnregister(MANAGER, contestC)).isFalse();
    }

    @Test
    void get_contestants() {
        assertThat(checker.canGetContestants(ADMIN, contestA)).isTrue();
        assertThat(checker.canGetContestants(ADMIN, contestB)).isTrue();
        assertThat(checker.canGetContestants(ADMIN, contestC)).isTrue();

        assertThat(checker.canGetContestants(USER, contestA)).isTrue();
        assertThat(checker.canGetContestants(USER, contestB)).isFalse();
        assertThat(checker.canGetContestants(USER, contestC)).isFalse();

        assertThat(checker.canGetContestants(CONTESTANT, contestA)).isTrue();
        assertThat(checker.canGetContestants(CONTESTANT, contestB)).isTrue();
        assertThat(checker.canGetContestants(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canGetContestants(SUPERVISOR, contestA)).isTrue();
        assertThat(checker.canGetContestants(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canGetContestants(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canGetContestants(MANAGER, contestA)).isTrue();
        assertThat(checker.canGetContestants(MANAGER, contestB)).isTrue();
        assertThat(checker.canGetContestants(MANAGER, contestC)).isFalse();
    }

    @Test
    void supervise_contestants() {
        assertThat(checker.canSuperviseContestants(ADMIN, contestA)).isTrue();
        assertThat(checker.canSuperviseContestants(ADMIN, contestB)).isTrue();
        assertThat(checker.canSuperviseContestants(ADMIN, contestC)).isTrue();

        assertThat(checker.canSuperviseContestants(USER, contestA)).isFalse();
        assertThat(checker.canSuperviseContestants(USER, contestB)).isFalse();
        assertThat(checker.canSuperviseContestants(USER, contestC)).isFalse();

        assertThat(checker.canSuperviseContestants(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canSuperviseContestants(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canSuperviseContestants(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canSuperviseContestants(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canSuperviseContestants(SUPERVISOR, contestB)).isFalse();
        addSupervisorToContestBWithPermission(SupervisorPermissionType.CONTESTANT);
        assertThat(checker.canSuperviseContestants(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canSuperviseContestants(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canSuperviseContestants(MANAGER, contestA)).isFalse();
        assertThat(checker.canSuperviseContestants(MANAGER, contestB)).isTrue();
        assertThat(checker.canSuperviseContestants(MANAGER, contestC)).isFalse();
    }
}
