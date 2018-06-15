package judgels.uriel.contest.submission;

import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.SUBMISSION;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.uriel.contest.role.AbstractRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestSubmissionRoleCheckerIntegrationTests extends AbstractRoleCheckerIntegrationTests {
    private ContestSubmissionRoleChecker checker;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestSubmissionRoleChecker();
    }

    @Test
    void view_submission() {
        assertThat(checker.canViewSubmission(ADMIN, contestB, CONTESTANT)).isTrue();

        assertThat(checker.canViewSubmission(CONTESTANT, contestB, CONTESTANT)).isFalse();
        assertThat(checker.canViewSubmission(CONTESTANT, contestBStarted, CONTESTANT)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewSubmission(CONTESTANT, contestBStarted, CONTESTANT)).isFalse();
        assertThat(checker.canViewSubmission(CONTESTANT, contestB, ANOTHER_CONTESTANT)).isFalse();
        assertThat(checker.canViewSubmission(CONTESTANT, contestBStarted, ANOTHER_CONTESTANT)).isFalse();

        assertThat(checker.canViewSubmission(SUPERVISOR, contestB, CONTESTANT)).isFalse();
        assertThat(checker.canViewSubmission(SUPERVISOR, contestBStarted, CONTESTANT)).isFalse();
        addSupervisorToContestBWithPermission(SUBMISSION);
        assertThat(checker.canViewSubmission(SUPERVISOR, contestB, CONTESTANT)).isTrue();
        assertThat(checker.canViewSubmission(SUPERVISOR, contestBStarted, CONTESTANT)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewSubmission(SUPERVISOR, contestB, CONTESTANT)).isTrue();
        assertThat(checker.canViewSubmission(SUPERVISOR, contestBStarted, CONTESTANT)).isTrue();

        assertThat(checker.canViewSubmission(MANAGER, contestB, CONTESTANT)).isTrue();
        assertThat(checker.canViewSubmission(MANAGER, contestBStarted, CONTESTANT)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewSubmission(MANAGER, contestB, CONTESTANT)).isTrue();
        assertThat(checker.canViewSubmission(MANAGER, contestBStarted, CONTESTANT)).isTrue();
    }

    @Test
    void view_own_submissions() {
        assertThat(checker.canViewOwnSubmissions(ADMIN, contestA)).isTrue();
        assertThat(checker.canViewOwnSubmissions(ADMIN, contestAStarted)).isTrue();
        assertThat(checker.canViewOwnSubmissions(ADMIN, contestB)).isTrue();
        assertThat(checker.canViewOwnSubmissions(ADMIN, contestBStarted)).isTrue();
        assertThat(checker.canViewOwnSubmissions(ADMIN, contestC)).isTrue();

        assertThat(checker.canViewOwnSubmissions(USER, contestA)).isFalse();
        assertThat(checker.canViewOwnSubmissions(USER, contestAStarted)).isFalse();
        assertThat(checker.canViewOwnSubmissions(USER, contestB)).isFalse();
        assertThat(checker.canViewOwnSubmissions(USER, contestBStarted)).isFalse();
        assertThat(checker.canViewOwnSubmissions(USER, contestC)).isFalse();

        assertThat(checker.canViewOwnSubmissions(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canViewOwnSubmissions(CONTESTANT, contestAStarted)).isFalse();
        assertThat(checker.canViewOwnSubmissions(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canViewOwnSubmissions(CONTESTANT, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewOwnSubmissions(CONTESTANT, contestBStarted)).isFalse();
        assertThat(checker.canViewOwnSubmissions(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canViewOwnSubmissions(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewOwnSubmissions(SUPERVISOR, contestAStarted)).isFalse();
        assertThat(checker.canViewOwnSubmissions(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canViewOwnSubmissions(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(checker.canViewOwnSubmissions(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(SUBMISSION);
        assertThat(checker.canViewOwnSubmissions(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewOwnSubmissions(SUPERVISOR, contestAStarted)).isFalse();
        assertThat(checker.canViewOwnSubmissions(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewOwnSubmissions(SUPERVISOR, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewOwnSubmissions(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewOwnSubmissions(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canViewOwnSubmissions(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canViewOwnSubmissions(MANAGER, contestA)).isFalse();
        assertThat(checker.canViewOwnSubmissions(MANAGER, contestAStarted)).isFalse();
        assertThat(checker.canViewOwnSubmissions(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewOwnSubmissions(MANAGER, contestBStarted)).isTrue();
        moduleStore.upsertPausedModule(contestBStarted.getJid());
        assertThat(checker.canViewOwnSubmissions(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewOwnSubmissions(MANAGER, contestBStarted)).isTrue();
        assertThat(checker.canViewOwnSubmissions(MANAGER, contestC)).isFalse();
    }

    @Test
    void supervise_submissions() {
        assertThat(checker.canSuperviseSubmissions(ADMIN, contestA)).isTrue();
        assertThat(checker.canSuperviseSubmissions(ADMIN, contestB)).isTrue();
        assertThat(checker.canSuperviseSubmissions(ADMIN, contestC)).isTrue();

        assertThat(checker.canSuperviseSubmissions(USER, contestA)).isFalse();
        assertThat(checker.canSuperviseSubmissions(USER, contestB)).isFalse();
        assertThat(checker.canSuperviseSubmissions(USER, contestC)).isFalse();

        assertThat(checker.canSuperviseSubmissions(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canSuperviseSubmissions(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canSuperviseSubmissions(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canSuperviseSubmissions(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canSuperviseSubmissions(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canSuperviseSubmissions(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(SUBMISSION);
        assertThat(checker.canSuperviseSubmissions(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canSuperviseSubmissions(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canSuperviseSubmissions(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canSuperviseSubmissions(MANAGER, contestA)).isFalse();
        assertThat(checker.canSuperviseSubmissions(MANAGER, contestB)).isTrue();
        assertThat(checker.canSuperviseSubmissions(MANAGER, contestC)).isFalse();
    }
}
