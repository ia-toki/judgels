package judgels.uriel.contest.problem;

import static java.time.temporal.ChronoUnit.HOURS;
import static judgels.persistence.TestClock.NOW;
import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.PROBLEM;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.api.contest.problem.ContestContestantProblem;
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
        assertThat(checker.canView(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canView(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canView(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(PROBLEM);
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
        assertThat(checker.canView(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canView(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(checker.canView(SUPERVISOR, contestBFinished)).isTrue();
        addSupervisorToContestBWithPermission(PROBLEM);
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
        assertThat(checker.canSupervise(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canSupervise(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(PROBLEM);
        assertThat(checker.canSupervise(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canSupervise(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canSupervise(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canSupervise(MANAGER, contestA)).isFalse();
        assertThat(checker.canSupervise(MANAGER, contestB)).isTrue();
        assertThat(checker.canSupervise(MANAGER, contestC)).isFalse();
    }

    @Test
    void submit() {
        ContestProblem problem = new ContestProblem.Builder()
                .problemJid("problemJid")
                .alias("alias")
                .status(ContestProblemStatus.OPEN)
                .submissionsLimit(50)
                .build();
        ContestContestantProblem contestantProblem = new ContestContestantProblem.Builder()
                .problem(problem)
                .totalSubmissions(10)
                .build();

        assertThat(checker.canSubmit(ADMIN, contestA, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(ADMIN, contestAStarted, contestantProblem)).isEmpty();
        assertThat(checker.canSubmit(ADMIN, contestB, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(ADMIN, contestBStarted, contestantProblem)).isEmpty();
        assertThat(checker.canSubmit(ADMIN, contestBFinished, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(ADMIN, contestC, contestantProblem)).isPresent();

        assertThat(checker.canSubmit(USER, contestA, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(USER, contestAStarted, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(USER, contestB, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(USER, contestBStarted, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(USER, contestBFinished, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(USER, contestC, contestantProblem)).isPresent();

        assertThat(checker.canSubmit(CONTESTANT, contestA, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(CONTESTANT, contestAStarted, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(CONTESTANT, contestB, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(CONTESTANT, contestBStarted, contestantProblem)).isEmpty();
        assertThat(checker.canSubmit(CONTESTANT, contestBFinished, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(CONTESTANT, contestC, contestantProblem)).isPresent();

        assertThat(checker.canSubmit(SUPERVISOR, contestA, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(SUPERVISOR, contestAStarted, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(SUPERVISOR, contestB, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(SUPERVISOR, contestBStarted, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(SUPERVISOR, contestBFinished, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(SUPERVISOR, contestC, contestantProblem)).isPresent();
        addSupervisorToContestBWithPermission(PROBLEM);
        assertThat(checker.canSubmit(SUPERVISOR, contestA, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(SUPERVISOR, contestAStarted, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(SUPERVISOR, contestB, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(SUPERVISOR, contestBStarted, contestantProblem)).isEmpty();
        assertThat(checker.canSubmit(SUPERVISOR, contestBFinished, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(SUPERVISOR, contestC, contestantProblem)).isPresent();

        assertThat(checker.canSubmit(MANAGER, contestA, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(MANAGER, contestAStarted, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(MANAGER, contestB, contestantProblem)).isPresent();
        assertThat(checker.canSubmit(MANAGER, contestBStarted, contestantProblem)).isEmpty();
        assertThat(checker.canSubmit(MANAGER, contestC, contestantProblem)).isPresent();

        ContestContestantProblem contestantProblemClosed = new ContestContestantProblem.Builder()
                .from(contestantProblem)
                .problem(new ContestProblem.Builder()
                        .from(contestantProblem.getProblem())
                        .status(ContestProblemStatus.CLOSED)
                        .build())
                .build();

        ContestContestantProblem contestantProblemLimitReached = new ContestContestantProblem.Builder()
                .from(contestantProblem)
                .problem(new ContestProblem.Builder()
                        .from(contestantProblem.getProblem())
                        .submissionsLimit(10)
                        .build())
                .build();

        ContestContestantProblem contestantProblemNoLimit = new ContestContestantProblem.Builder()
                .from(contestantProblem)
                .problem(new ContestProblem.Builder()
                        .from(contestantProblem.getProblem())
                        .submissionsLimit(0)
                        .build())
                .build();

        assertThat(checker.canSubmit(CONTESTANT, contestA, contestantProblem))
                .contains("You are not a contestant.");
        assertThat(checker.canSubmit(CONTESTANT, contestB, contestantProblem))
                .contains("Contest has not started yet.");
        assertThat(checker.canSubmit(CONTESTANT, contestBFinished, contestantProblem))
                .contains("Contest is over.");
        assertThat(checker.canSubmit(CONTESTANT, contestBStarted, contestantProblemClosed))
                .contains("Problem is closed.");
        assertThat(checker.canSubmit(CONTESTANT, contestBStarted, contestantProblemLimitReached))
                .contains("Submissions limit has been reached.");
        assertThat(checker.canSubmit(CONTESTANT, contestBStarted, contestantProblemNoLimit))
                .isEmpty();
    }
}
