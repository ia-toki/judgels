package judgels.uriel.contest.submission;

import static java.time.temporal.ChronoUnit.HOURS;
import static judgels.persistence.TestClock.NOW;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.SUBMISSION;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.contest.AbstractContestRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestSubmissionRoleCheckerIntegrationTests extends AbstractContestRoleCheckerIntegrationTests {
    private ContestSubmissionRoleChecker checker;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestSubmissionRoleChecker();
    }

    @Test
    void view() {
        assertThat(checker.canView(ADMIN, contestB, CONTESTANT)).isTrue();

        assertThat(checker.canView(CONTESTANT, contestB, CONTESTANT)).isFalse();
        assertThat(checker.canView(CONTESTANT, contestBStarted, CONTESTANT)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canView(CONTESTANT, contestBStarted, CONTESTANT)).isFalse();
        assertThat(checker.canView(CONTESTANT, contestB, ANOTHER_CONTESTANT)).isFalse();
        assertThat(checker.canView(CONTESTANT, contestBStarted, ANOTHER_CONTESTANT)).isFalse();

        assertThat(checker.canView(SUPERVISOR, contestB, CONTESTANT)).isTrue();
        assertThat(checker.canView(SUPERVISOR, contestBStarted, CONTESTANT)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canView(SUPERVISOR, contestBStarted, CONTESTANT)).isTrue();

        assertThat(checker.canView(MANAGER, contestB, CONTESTANT)).isTrue();
        assertThat(checker.canView(MANAGER, contestBStarted, CONTESTANT)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canView(MANAGER, contestBStarted, CONTESTANT)).isTrue();
    }

    @Test
    void view_own() {
        assertThat(checker.canViewOwn(ADMIN, contestA)).isTrue();
        assertThat(checker.canViewOwn(ADMIN, contestAStarted)).isTrue();
        assertThat(checker.canViewOwn(ADMIN, contestB)).isTrue();
        assertThat(checker.canViewOwn(ADMIN, contestBStarted)).isTrue();
        assertThat(checker.canViewOwn(ADMIN, contestC)).isTrue();

        assertThat(checker.canViewOwn(USER, contestA)).isFalse();
        assertThat(checker.canViewOwn(USER, contestAStarted)).isFalse();
        assertThat(checker.canViewOwn(USER, contestB)).isFalse();
        assertThat(checker.canViewOwn(USER, contestBStarted)).isFalse();
        assertThat(checker.canViewOwn(USER, contestC)).isFalse();

        assertThat(checker.canViewOwn(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canViewOwn(CONTESTANT, contestAStarted)).isFalse();
        assertThat(checker.canViewOwn(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canViewOwn(CONTESTANT, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewOwn(CONTESTANT, contestBStarted)).isFalse();
        assertThat(checker.canViewOwn(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canViewOwn(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewOwn(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewOwn(SUPERVISOR, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewOwn(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canViewOwn(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canViewOwn(MANAGER, contestA)).isFalse();
        assertThat(checker.canViewOwn(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewOwn(MANAGER, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewOwn(MANAGER, contestBStarted)).isTrue();
        assertThat(checker.canViewOwn(MANAGER, contestC)).isFalse();
    }

    @Test
    void view_all() {
        Contest contestAFinished;
        contestAFinished = contestStore.createContest(
                new ContestCreateData.Builder().slug("contest-a-finished").build());
        contestAFinished = contestStore.updateContest(
                contestAFinished.getJid(),
                new ContestUpdateData.Builder().beginTime(NOW.minus(10, HOURS)).build()).get();

        moduleStore.upsertRegistrationModule(contestAFinished.getJid());

        assertThat(checker.canViewAll(contestA)).isFalse();
        assertThat(checker.canViewAll(contestAStarted)).isFalse();
        assertThat(checker.canViewAll(contestAFinished)).isTrue();
        assertThat(checker.canViewAll(contestB)).isFalse();
        assertThat(checker.canViewAll(contestBStarted)).isFalse();
        assertThat(checker.canViewAll(contestC)).isFalse();
    }

    @Test
    void supervise() {
        assertThat(checker.canSupervise(ADMIN, contestA)).isTrue();
        assertThat(checker.canSupervise(ADMIN, contestB)).isTrue();
        assertThat(checker.canSupervise(ADMIN, contestC)).isTrue();

        assertThat(checker.canSupervise(USER, contestA)).isFalse();
        assertThat(checker.canSupervise(USER, contestB)).isFalse();
        assertThat(checker.canSupervise(USER, contestC)).isFalse();

        assertThat(checker.canSupervise(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canSupervise(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canSupervise(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canSupervise(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canSupervise(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canSupervise(SUPERVISOR, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canSupervise(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canSupervise(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canSupervise(MANAGER, contestA)).isFalse();
        assertThat(checker.canSupervise(MANAGER, contestB)).isTrue();
        assertThat(checker.canSupervise(MANAGER, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canSupervise(MANAGER, contestBStarted)).isTrue();
        assertThat(checker.canSupervise(MANAGER, contestC)).isFalse();
    }

    @Test
    void manage() {
        assertThat(checker.canManage(ADMIN, contestA)).isTrue();
        assertThat(checker.canManage(ADMIN, contestB)).isTrue();
        assertThat(checker.canManage(ADMIN, contestC)).isTrue();

        assertThat(checker.canManage(USER, contestA)).isFalse();
        assertThat(checker.canManage(USER, contestB)).isFalse();
        assertThat(checker.canManage(USER, contestC)).isFalse();

        assertThat(checker.canManage(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canManage(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canManage(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canManage(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canManage(SUPERVISOR, contestB)).isFalse();
        addSupervisorToContestBWithPermission(SUBMISSION);
        assertThat(checker.canManage(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canManage(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canManage(MANAGER, contestA)).isFalse();
        assertThat(checker.canManage(MANAGER, contestB)).isTrue();
        assertThat(checker.canManage(MANAGER, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canManage(MANAGER, contestBStarted)).isTrue();
        assertThat(checker.canManage(MANAGER, contestC)).isFalse();
    }
}
