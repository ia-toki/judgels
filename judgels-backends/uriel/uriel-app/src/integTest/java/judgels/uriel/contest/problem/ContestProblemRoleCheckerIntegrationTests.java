package judgels.uriel.contest.problem;

import static java.time.temporal.ChronoUnit.HOURS;
import static judgels.persistence.TestClock.NOW;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.PROBLEM;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.contest.role.AbstractRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestProblemRoleCheckerIntegrationTests extends AbstractRoleCheckerIntegrationTests {
    private ContestProblemRoleChecker checker;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestProblemRoleChecker();
    }

    @Test
    void view() {
        assertThat(checker.canView(ADMIN, contestA)).isTrue();
        assertThat(checker.canView(ADMIN, contestAStarted)).isTrue();
        assertThat(checker.canView(ADMIN, contestB)).isTrue();
        assertThat(checker.canView(ADMIN, contestBStarted)).isTrue();
        assertThat(checker.canView(ADMIN, contestC)).isTrue();

        assertThat(checker.canView(USER, contestA)).isFalse();
        assertThat(checker.canView(USER, contestAStarted)).isTrue();
        assertThat(checker.canView(USER, contestB)).isFalse();
        assertThat(checker.canView(USER, contestBStarted)).isFalse();
        assertThat(checker.canView(USER, contestC)).isFalse();

        assertThat(checker.canView(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canView(CONTESTANT, contestAStarted)).isTrue();
        assertThat(checker.canView(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canView(CONTESTANT, contestBStarted)).isTrue();
        assertThat(checker.canView(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canView(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canView(SUPERVISOR, contestAStarted)).isTrue();
        assertThat(checker.canView(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canView(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canView(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canView(MANAGER, contestA)).isFalse();
        assertThat(checker.canView(MANAGER, contestAStarted)).isTrue();
        assertThat(checker.canView(MANAGER, contestB)).isTrue();
        assertThat(checker.canView(MANAGER, contestBStarted)).isTrue();
        assertThat(checker.canView(MANAGER, contestC)).isFalse();
    }

    @Test
    void view_virtual() {
        Contest contestAFinished = contestStore.createContest(
                new ContestCreateData.Builder().slug("contest-a-finished").build());
        contestAFinished = contestStore.updateContest(
                contestAFinished.getJid(),
                new ContestUpdateData.Builder().beginTime(NOW.minus(10, HOURS)).build()).get();

        moduleStore.upsertVirtualModule(
                contestA.getJid(),
                new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(1)).build());
        moduleStore.upsertVirtualModule(
                contestAStarted.getJid(),
                new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(1)).build());
        moduleStore.upsertRegistrationModule(contestAFinished.getJid());
        moduleStore.upsertVirtualModule(
                contestAFinished.getJid(),
                new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(1)).build());
        moduleStore.upsertVirtualModule(
                contestB.getJid(),
                new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(1)).build());
        moduleStore.upsertVirtualModule(
                contestBStarted.getJid(),
                new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(1)).build());
        moduleStore.upsertVirtualModule(
                contestBFinished.getJid(),
                new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(1)).build());

        contestantStore.startVirtualContest(contestBStarted.getJid(), CONTESTANT);

        assertThat(checker.canView(ADMIN, contestA)).isTrue();
        assertThat(checker.canView(ADMIN, contestAStarted)).isTrue();
        assertThat(checker.canView(ADMIN, contestAFinished)).isTrue();
        assertThat(checker.canView(ADMIN, contestB)).isTrue();
        assertThat(checker.canView(ADMIN, contestBStarted)).isTrue();
        assertThat(checker.canView(ADMIN, contestBFinished)).isTrue();

        assertThat(checker.canView(USER, contestA)).isFalse();
        assertThat(checker.canView(USER, contestAStarted)).isFalse();
        assertThat(checker.canView(USER, contestAFinished)).isTrue();
        assertThat(checker.canView(USER, contestB)).isFalse();
        assertThat(checker.canView(USER, contestBStarted)).isFalse();
        assertThat(checker.canView(USER, contestBFinished)).isFalse();

        assertThat(checker.canView(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canView(CONTESTANT, contestAStarted)).isFalse();
        assertThat(checker.canView(CONTESTANT, contestAFinished)).isTrue();
        assertThat(checker.canView(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canView(CONTESTANT, contestBStarted)).isTrue();
        assertThat(checker.canView(CONTESTANT, contestBFinished)).isTrue();

        assertThat(checker.canView(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canView(SUPERVISOR, contestAStarted)).isFalse();
        assertThat(checker.canView(SUPERVISOR, contestAFinished)).isTrue();
        assertThat(checker.canView(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canView(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canView(SUPERVISOR, contestBFinished)).isTrue();

        assertThat(checker.canView(MANAGER, contestA)).isFalse();
        assertThat(checker.canView(MANAGER, contestAStarted)).isFalse();
        assertThat(checker.canView(MANAGER, contestAFinished)).isTrue();
        assertThat(checker.canView(MANAGER, contestB)).isTrue();
        assertThat(checker.canView(MANAGER, contestBStarted)).isTrue();
        assertThat(checker.canView(MANAGER, contestBFinished)).isTrue();
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
        assertThat(checker.canSupervise(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canSupervise(MANAGER, contestA)).isFalse();
        assertThat(checker.canSupervise(MANAGER, contestB)).isTrue();
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
        addSupervisorToContestBWithPermission(PROBLEM);
        assertThat(checker.canManage(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canManage(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canManage(MANAGER, contestA)).isFalse();
        assertThat(checker.canManage(MANAGER, contestB)).isTrue();
        assertThat(checker.canManage(MANAGER, contestC)).isFalse();
    }

    @Test
    void submit() {
        ContestProblem problem = new ContestProblem.Builder()
                .problemJid("problemJid")
                .alias("alias")
                .status(ContestProblemStatus.OPEN)
                .submissionsLimit(50)
                .build();

        assertThat(checker.canSubmit(ADMIN, contestA, problem, 10)).isEmpty();
        assertThat(checker.canSubmit(ADMIN, contestAStarted, problem, 10)).isEmpty();
        assertThat(checker.canSubmit(ADMIN, contestB, problem, 10)).isEmpty();
        assertThat(checker.canSubmit(ADMIN, contestBStarted, problem, 10)).isEmpty();
        assertThat(checker.canSubmit(ADMIN, contestBFinished, problem, 10)).isPresent();
        assertThat(checker.canSubmit(ADMIN, contestC, problem, 10)).isEmpty();

        assertThat(checker.canSubmit(USER, contestA, problem, 10)).isPresent();
        assertThat(checker.canSubmit(USER, contestAStarted, problem, 10)).isPresent();
        assertThat(checker.canSubmit(USER, contestB, problem, 10)).isPresent();
        assertThat(checker.canSubmit(USER, contestBStarted, problem, 10)).isPresent();
        assertThat(checker.canSubmit(USER, contestBFinished, problem, 10)).isPresent();
        assertThat(checker.canSubmit(USER, contestC, problem, 10)).isPresent();

        assertThat(checker.canSubmit(CONTESTANT, contestA, problem, 10)).isPresent();
        assertThat(checker.canSubmit(CONTESTANT, contestAStarted, problem, 10)).isPresent();
        assertThat(checker.canSubmit(CONTESTANT, contestB, problem, 10)).isPresent();
        assertThat(checker.canSubmit(CONTESTANT, contestBStarted, problem, 10)).isEmpty();
        assertThat(checker.canSubmit(CONTESTANT, contestBFinished, problem, 10)).isPresent();
        assertThat(checker.canSubmit(CONTESTANT, contestC, problem, 10)).isPresent();

        assertThat(checker.canSubmit(SUPERVISOR, contestA, problem, 10)).isPresent();
        assertThat(checker.canSubmit(SUPERVISOR, contestAStarted, problem, 10)).isPresent();
        assertThat(checker.canSubmit(SUPERVISOR, contestB, problem, 10)).isEmpty();
        assertThat(checker.canSubmit(SUPERVISOR, contestBStarted, problem, 10)).isEmpty();
        assertThat(checker.canSubmit(SUPERVISOR, contestBFinished, problem, 10)).isPresent();
        assertThat(checker.canSubmit(SUPERVISOR, contestC, problem, 10)).isPresent();

        assertThat(checker.canSubmit(MANAGER, contestA, problem, 10)).isPresent();
        assertThat(checker.canSubmit(MANAGER, contestAStarted, problem, 10)).isPresent();
        assertThat(checker.canSubmit(MANAGER, contestB, problem, 10)).isEmpty();
        assertThat(checker.canSubmit(MANAGER, contestBStarted, problem, 10)).isEmpty();
        assertThat(checker.canSubmit(MANAGER, contestC, problem, 10)).isPresent();

        ContestProblem problemClosed = new ContestProblem.Builder()
                .from(problem)
                .status(ContestProblemStatus.CLOSED)
                .build();

        ContestProblem problemLimitReached = new ContestProblem.Builder()
                .from(problem)
                .submissionsLimit(10)
                .build();

        ContestProblem problemNoLimit = new ContestProblem.Builder()
                .from(problem)
                .build();

        assertThat(checker.canSubmit(CONTESTANT, contestA, problem, 10))
                .contains("You are not a contestant.");
        assertThat(checker.canSubmit(CONTESTANT, contestB, problem, 10))
                .contains("Contest has not started yet.");
        assertThat(checker.canSubmit(CONTESTANT, contestBFinished, problem, 10))
                .contains("Contest is over.");
        assertThat(checker.canSubmit(CONTESTANT, contestBStarted, problemClosed, 10))
                .contains("Problem is closed.");
        assertThat(checker.canSubmit(CONTESTANT, contestBStarted, problemLimitReached, 10L))
                .contains("Submissions limit has been reached.");
        assertThat(checker.canSubmit(CONTESTANT, contestBStarted, problemNoLimit, 10))
                .isEmpty();
    }
}
