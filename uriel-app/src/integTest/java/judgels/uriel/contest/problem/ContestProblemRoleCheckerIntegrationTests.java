package judgels.uriel.contest.problem;

import static java.time.temporal.ChronoUnit.HOURS;
import static judgels.uriel.UrielIntegrationTestPersistenceModule.NOW;
import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.PROBLEM;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
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
    void before(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestProblemRoleChecker();
    }

    @Test
    void view_problems() {
        assertThat(checker.canViewProblems(ADMIN, contestA)).isTrue();
        assertThat(checker.canViewProblems(ADMIN, contestAStarted)).isTrue();
        assertThat(checker.canViewProblems(ADMIN, contestB)).isTrue();
        assertThat(checker.canViewProblems(ADMIN, contestBStarted)).isTrue();
        assertThat(checker.canViewProblems(ADMIN, contestC)).isTrue();

        assertThat(checker.canViewProblems(USER, contestA)).isFalse();
        assertThat(checker.canViewProblems(USER, contestAStarted)).isTrue();
        assertThat(checker.canViewProblems(USER, contestB)).isFalse();
        assertThat(checker.canViewProblems(USER, contestBStarted)).isFalse();
        assertThat(checker.canViewProblems(USER, contestC)).isFalse();

        assertThat(checker.canViewProblems(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canViewProblems(CONTESTANT, contestAStarted)).isTrue();
        assertThat(checker.canViewProblems(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canViewProblems(CONTESTANT, contestBStarted)).isTrue();
        assertThat(checker.canViewProblems(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canViewProblems(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewProblems(SUPERVISOR, contestAStarted)).isTrue();
        assertThat(checker.canViewProblems(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canViewProblems(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canViewProblems(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(PROBLEM);
        assertThat(checker.canViewProblems(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewProblems(SUPERVISOR, contestAStarted)).isTrue();
        assertThat(checker.canViewProblems(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewProblems(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canViewProblems(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canViewProblems(MANAGER, contestA)).isFalse();
        assertThat(checker.canViewProblems(MANAGER, contestAStarted)).isTrue();
        assertThat(checker.canViewProblems(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewProblems(MANAGER, contestBStarted)).isTrue();
        assertThat(checker.canViewProblems(MANAGER, contestC)).isFalse();
    }

    @Test
    void view_problems_virtual() {
        Contest contestAFinished = contestStore.createContest(new ContestData.Builder()
                .name("Contest A - Ended")
                .beginTime(NOW.minus(10, HOURS))
                .build());

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

        assertThat(checker.canViewProblems(ADMIN, contestA)).isTrue();
        assertThat(checker.canViewProblems(ADMIN, contestAStarted)).isTrue();
        assertThat(checker.canViewProblems(ADMIN, contestAFinished)).isTrue();
        assertThat(checker.canViewProblems(ADMIN, contestB)).isTrue();
        assertThat(checker.canViewProblems(ADMIN, contestBStarted)).isTrue();
        assertThat(checker.canViewProblems(ADMIN, contestBFinished)).isTrue();

        assertThat(checker.canViewProblems(USER, contestA)).isFalse();
        assertThat(checker.canViewProblems(USER, contestAStarted)).isFalse();
        assertThat(checker.canViewProblems(USER, contestAFinished)).isTrue();
        assertThat(checker.canViewProblems(USER, contestB)).isFalse();
        assertThat(checker.canViewProblems(USER, contestBStarted)).isFalse();
        assertThat(checker.canViewProblems(USER, contestBFinished)).isFalse();

        assertThat(checker.canViewProblems(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canViewProblems(CONTESTANT, contestAStarted)).isFalse();
        assertThat(checker.canViewProblems(CONTESTANT, contestAFinished)).isTrue();
        assertThat(checker.canViewProblems(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canViewProblems(CONTESTANT, contestBStarted)).isTrue();
        assertThat(checker.canViewProblems(CONTESTANT, contestBFinished)).isTrue();

        assertThat(checker.canViewProblems(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewProblems(SUPERVISOR, contestAStarted)).isFalse();
        assertThat(checker.canViewProblems(SUPERVISOR, contestAFinished)).isTrue();
        assertThat(checker.canViewProblems(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canViewProblems(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(checker.canViewProblems(SUPERVISOR, contestBFinished)).isTrue();
        addSupervisorToContestBWithPermission(PROBLEM);
        assertThat(checker.canViewProblems(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canViewProblems(SUPERVISOR, contestAStarted)).isFalse();
        assertThat(checker.canViewProblems(SUPERVISOR, contestAFinished)).isTrue();
        assertThat(checker.canViewProblems(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewProblems(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(checker.canViewProblems(SUPERVISOR, contestBFinished)).isTrue();

        assertThat(checker.canViewProblems(MANAGER, contestA)).isFalse();
        assertThat(checker.canViewProblems(MANAGER, contestAStarted)).isFalse();
        assertThat(checker.canViewProblems(MANAGER, contestAFinished)).isTrue();
        assertThat(checker.canViewProblems(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewProblems(MANAGER, contestBStarted)).isTrue();
        assertThat(checker.canViewProblems(MANAGER, contestBFinished)).isTrue();
    }

    @Test
    void supervise_problems() {
        assertThat(checker.canSuperviseProblems(ADMIN, contestA)).isTrue();
        assertThat(checker.canSuperviseProblems(ADMIN, contestB)).isTrue();
        assertThat(checker.canSuperviseProblems(ADMIN, contestC)).isTrue();

        assertThat(checker.canSuperviseProblems(USER, contestA)).isFalse();
        assertThat(checker.canSuperviseProblems(USER, contestB)).isFalse();
        assertThat(checker.canSuperviseProblems(USER, contestC)).isFalse();

        assertThat(checker.canSuperviseProblems(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canSuperviseProblems(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canSuperviseProblems(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canSuperviseProblems(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canSuperviseProblems(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canSuperviseProblems(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(PROBLEM);
        assertThat(checker.canSuperviseProblems(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canSuperviseProblems(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canSuperviseProblems(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canSuperviseProblems(MANAGER, contestA)).isFalse();
        assertThat(checker.canSuperviseProblems(MANAGER, contestB)).isTrue();
        assertThat(checker.canSuperviseProblems(MANAGER, contestC)).isFalse();
    }

    @Test
    void submit_problem() {
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

        assertThat(checker.canSubmitProblem(ADMIN, contestA, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(ADMIN, contestAStarted, contestantProblem)).isEmpty();
        assertThat(checker.canSubmitProblem(ADMIN, contestB, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(ADMIN, contestBStarted, contestantProblem)).isEmpty();
        assertThat(checker.canSubmitProblem(ADMIN, contestBFinished, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(ADMIN, contestC, contestantProblem)).isPresent();

        assertThat(checker.canSubmitProblem(USER, contestA, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(USER, contestAStarted, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(USER, contestB, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(USER, contestBStarted, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(USER, contestBFinished, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(USER, contestC, contestantProblem)).isPresent();

        assertThat(checker.canSubmitProblem(CONTESTANT, contestA, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(CONTESTANT, contestAStarted, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(CONTESTANT, contestB, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(CONTESTANT, contestBStarted, contestantProblem)).isEmpty();
        assertThat(checker.canSubmitProblem(CONTESTANT, contestBFinished, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(CONTESTANT, contestC, contestantProblem)).isPresent();

        assertThat(checker.canSubmitProblem(SUPERVISOR, contestA, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(SUPERVISOR, contestAStarted, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(SUPERVISOR, contestB, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(SUPERVISOR, contestBStarted, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(SUPERVISOR, contestBFinished, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(SUPERVISOR, contestC, contestantProblem)).isPresent();
        addSupervisorToContestBWithPermission(PROBLEM);
        assertThat(checker.canSubmitProblem(SUPERVISOR, contestA, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(SUPERVISOR, contestAStarted, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(SUPERVISOR, contestB, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(SUPERVISOR, contestBStarted, contestantProblem)).isEmpty();
        assertThat(checker.canSubmitProblem(SUPERVISOR, contestBFinished, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(SUPERVISOR, contestC, contestantProblem)).isPresent();

        assertThat(checker.canSubmitProblem(MANAGER, contestA, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(MANAGER, contestAStarted, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(MANAGER, contestB, contestantProblem)).isPresent();
        assertThat(checker.canSubmitProblem(MANAGER, contestBStarted, contestantProblem)).isEmpty();
        assertThat(checker.canSubmitProblem(MANAGER, contestC, contestantProblem)).isPresent();

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

        assertThat(checker.canSubmitProblem(CONTESTANT, contestA, contestantProblem))
                .contains("You are not a contestant.");
        assertThat(checker.canSubmitProblem(CONTESTANT, contestB, contestantProblem))
                .contains("Contest has not started yet.");
        assertThat(checker.canSubmitProblem(CONTESTANT, contestBFinished, contestantProblem))
                .contains("Contest is over.");
        assertThat(checker.canSubmitProblem(CONTESTANT, contestBStarted, contestantProblemClosed))
                .contains("Problem is closed.");
        assertThat(checker.canSubmitProblem(CONTESTANT, contestBStarted, contestantProblemLimitReached))
                .contains("Submissions limit has been reached.");
        assertThat(checker.canSubmitProblem(CONTESTANT, contestBStarted, contestantProblemNoLimit))
                .isEmpty();
    }
}
