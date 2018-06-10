package judgels.uriel.contest.role;

import static java.time.temporal.ChronoUnit.HOURS;
import static judgels.uriel.UrielIntegrationTestPersistenceModule.NOW;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.DaggerUrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestHibernateModule;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.api.contest.problem.ContestContestantProblem;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.api.contest.supervisor.ContestSupervisorData;
import judgels.uriel.api.contest.supervisor.SupervisorPermission;
import judgels.uriel.api.contest.supervisor.SupervisorPermissionType;
import judgels.uriel.contest.ContestStore;
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
import judgels.uriel.role.RoleChecker;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {
        AdminRoleModel.class,
        ContestModel.class,
        ContestModuleModel.class,
        ContestContestantModel.class,
        ContestSupervisorModel.class,
        ContestManagerModel.class})
class RoleCheckerIntegrationTests {
    private static final String USER = "userJid";
    private static final String ADMIN = "adminJid";
    private static final String CONTESTANT = "contestantJid";
    private static final String ANOTHER_CONTESTANT = "anotherContestantJid";
    private static final String SUPERVISOR = "supervisorJid";
    private static final String MANAGER = "managerJid";

    private Contest contestA;
    private Contest contestAStarted;
    private Contest contestB;
    private Contest contestBStarted;
    private Contest contestBFinished;
    private Contest contestC;

    private ContestStore contestStore;
    private ContestModuleStore moduleStore;
    private ContestContestantStore contestantStore;
    private ContestSupervisorStore supervisorStore;
    private ContestManagerStore managerStore;

    private RoleChecker roleChecker;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = DaggerUrielIntegrationTestComponent.builder()
                .urielIntegrationTestHibernateModule(new UrielIntegrationTestHibernateModule(sessionFactory))
                .build();

        AdminRoleStore adminRoleStore = component.adminRoleStore();
        contestStore = component.contestStore();
        moduleStore = component.contestModuleStore();
        contestantStore = component.contestContestantStore();
        supervisorStore = component.contestSupervisorStore();
        managerStore = component.contestManagerStore();

        roleChecker = component.roleChecker();

        adminRoleStore.addAdmin(ADMIN);

        prepareContestA();
        prepareContestB();
        prepareContestC();
    }

    @Test
    void create_contest() {
        assertThat(roleChecker.canCreateContest(ADMIN)).isTrue();
        assertThat(roleChecker.canCreateContest(USER)).isFalse();
        assertThat(roleChecker.canCreateContest(CONTESTANT)).isFalse();
        assertThat(roleChecker.canCreateContest(SUPERVISOR)).isFalse();
        assertThat(roleChecker.canCreateContest(MANAGER)).isFalse();
    }

    @Test
    void view_contest() {
        assertThat(roleChecker.canViewContest(ADMIN, contestA)).isTrue();
        assertThat(roleChecker.canViewContest(ADMIN, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewContest(ADMIN, contestB)).isTrue();
        assertThat(roleChecker.canViewContest(ADMIN, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewContest(ADMIN, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewContest(ADMIN, contestC)).isTrue();

        assertThat(roleChecker.canViewContest(USER, contestA)).isTrue();
        assertThat(roleChecker.canViewContest(USER, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewContest(USER, contestB)).isFalse();
        assertThat(roleChecker.canViewContest(USER, contestC)).isFalse();

        assertThat(roleChecker.canViewContest(CONTESTANT, contestA)).isTrue();
        assertThat(roleChecker.canViewContest(CONTESTANT, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewContest(CONTESTANT, contestB)).isTrue();
        assertThat(roleChecker.canViewContest(CONTESTANT, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewContest(CONTESTANT, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewContest(CONTESTANT, contestC)).isFalse();

        assertThat(roleChecker.canViewContest(SUPERVISOR, contestA)).isTrue();
        assertThat(roleChecker.canViewContest(SUPERVISOR, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewContest(SUPERVISOR, contestB)).isTrue();
        assertThat(roleChecker.canViewContest(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewContest(SUPERVISOR, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewContest(SUPERVISOR, contestC)).isFalse();

        assertThat(roleChecker.canViewContest(MANAGER, contestA)).isTrue();
        assertThat(roleChecker.canViewContest(MANAGER, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewContest(MANAGER, contestB)).isTrue();
        assertThat(roleChecker.canViewContest(MANAGER, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewContest(MANAGER, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewContest(MANAGER, contestC)).isFalse();
    }

    @Test
    void start_virtual_contest() {
        moduleStore.upsertVirtualModule(
                contestB.getJid(),
                new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(1)).build());
        moduleStore.upsertVirtualModule(
                contestBStarted.getJid(),
                new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(1)).build());

        contestantStore.startVirtualContest(contestBStarted.getJid(), ANOTHER_CONTESTANT);

        assertThat(roleChecker.canStartVirtualContest(CONTESTANT, contestB)).isFalse();
        assertThat(roleChecker.canStartVirtualContest(CONTESTANT, contestBStarted)).isTrue();
        assertThat(roleChecker.canStartVirtualContest(ANOTHER_CONTESTANT, contestBStarted)).isFalse();
    }

    @Test
    void view_published_announcements() {
        assertThat(roleChecker.canViewPublishedAnnouncements(ADMIN, contestA)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(ADMIN, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(ADMIN, contestB)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(ADMIN, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(ADMIN, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(ADMIN, contestC)).isTrue();

        assertThat(roleChecker.canViewPublishedAnnouncements(USER, contestA)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(USER, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(USER, contestB)).isFalse();
        assertThat(roleChecker.canViewPublishedAnnouncements(USER, contestC)).isFalse();

        assertThat(roleChecker.canViewPublishedAnnouncements(CONTESTANT, contestA)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(CONTESTANT, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(CONTESTANT, contestB)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(CONTESTANT, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(CONTESTANT, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(CONTESTANT, contestC)).isFalse();

        assertThat(roleChecker.canViewPublishedAnnouncements(SUPERVISOR, contestA)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(SUPERVISOR, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(SUPERVISOR, contestB)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(SUPERVISOR, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(SupervisorPermissionType.ANNOUNCEMENT);
        assertThat(roleChecker.canViewPublishedAnnouncements(SUPERVISOR, contestA)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(SUPERVISOR, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(SUPERVISOR, contestB)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(SUPERVISOR, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(SUPERVISOR, contestC)).isFalse();

        assertThat(roleChecker.canViewPublishedAnnouncements(MANAGER, contestA)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(MANAGER, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(MANAGER, contestB)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(MANAGER, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(MANAGER, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewPublishedAnnouncements(MANAGER, contestC)).isFalse();
    }

    @Test
    void view_own_clarifications() {
        assertThat(roleChecker.canViewOwnClarifications(ADMIN, contestA)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(ADMIN, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(ADMIN, contestB)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(ADMIN, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(ADMIN, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(ADMIN, contestC)).isTrue();

        assertThat(roleChecker.canViewOwnClarifications(USER, contestA)).isFalse();
        assertThat(roleChecker.canViewOwnClarifications(USER, contestAStarted)).isFalse();
        assertThat(roleChecker.canViewOwnClarifications(USER, contestB)).isFalse();
        assertThat(roleChecker.canViewOwnClarifications(USER, contestC)).isFalse();

        assertThat(roleChecker.canViewOwnClarifications(CONTESTANT, contestA)).isFalse();
        assertThat(roleChecker.canViewOwnClarifications(CONTESTANT, contestB)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(CONTESTANT, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(CONTESTANT, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(CONTESTANT, contestC)).isFalse();

        assertThat(roleChecker.canViewOwnClarifications(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canViewOwnClarifications(SUPERVISOR, contestB)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(SUPERVISOR, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(SupervisorPermissionType.CLARIFICATION);
        assertThat(roleChecker.canViewOwnClarifications(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canViewOwnClarifications(SUPERVISOR, contestB)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(SUPERVISOR, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(SUPERVISOR, contestC)).isFalse();

        assertThat(roleChecker.canViewOwnClarifications(MANAGER, contestA)).isFalse();
        assertThat(roleChecker.canViewOwnClarifications(MANAGER, contestB)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(MANAGER, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(MANAGER, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewOwnClarifications(MANAGER, contestC)).isFalse();
    }

    @Test
    void view_problems() {
        assertThat(roleChecker.canViewProblems(ADMIN, contestA)).isTrue();
        assertThat(roleChecker.canViewProblems(ADMIN, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewProblems(ADMIN, contestB)).isTrue();
        assertThat(roleChecker.canViewProblems(ADMIN, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewProblems(ADMIN, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewProblems(ADMIN, contestC)).isTrue();

        assertThat(roleChecker.canViewProblems(USER, contestA)).isFalse();
        assertThat(roleChecker.canViewProblems(USER, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewProblems(USER, contestB)).isFalse();
        assertThat(roleChecker.canViewProblems(USER, contestC)).isFalse();

        assertThat(roleChecker.canViewProblems(CONTESTANT, contestA)).isFalse();
        assertThat(roleChecker.canViewProblems(CONTESTANT, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewProblems(CONTESTANT, contestB)).isFalse();
        assertThat(roleChecker.canViewProblems(CONTESTANT, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewProblems(CONTESTANT, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewProblems(CONTESTANT, contestC)).isFalse();

        assertThat(roleChecker.canViewProblems(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canViewProblems(SUPERVISOR, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewProblems(SUPERVISOR, contestB)).isFalse();
        assertThat(roleChecker.canViewProblems(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewProblems(SUPERVISOR, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewProblems(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(SupervisorPermissionType.PROBLEM);
        assertThat(roleChecker.canViewProblems(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canViewProblems(SUPERVISOR, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewProblems(SUPERVISOR, contestB)).isTrue();
        assertThat(roleChecker.canViewProblems(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewProblems(SUPERVISOR, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewProblems(SUPERVISOR, contestC)).isFalse();

        assertThat(roleChecker.canViewProblems(MANAGER, contestA)).isFalse();
        assertThat(roleChecker.canViewProblems(MANAGER, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewProblems(MANAGER, contestB)).isTrue();
        assertThat(roleChecker.canViewProblems(MANAGER, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewProblems(MANAGER, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewProblems(MANAGER, contestC)).isFalse();
    }

    @Test
    void supervise_problems() {
        assertThat(roleChecker.canSuperviseProblems(ADMIN, contestA)).isTrue();
        assertThat(roleChecker.canSuperviseProblems(ADMIN, contestAStarted)).isTrue();
        assertThat(roleChecker.canSuperviseProblems(ADMIN, contestB)).isTrue();
        assertThat(roleChecker.canSuperviseProblems(ADMIN, contestBStarted)).isTrue();
        assertThat(roleChecker.canSuperviseProblems(ADMIN, contestBFinished)).isTrue();
        assertThat(roleChecker.canSuperviseProblems(ADMIN, contestC)).isTrue();

        assertThat(roleChecker.canSuperviseProblems(USER, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(USER, contestAStarted)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(USER, contestB)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(USER, contestBStarted)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(USER, contestC)).isFalse();

        assertThat(roleChecker.canSuperviseProblems(CONTESTANT, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(CONTESTANT, contestAStarted)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(CONTESTANT, contestB)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(CONTESTANT, contestBStarted)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(CONTESTANT, contestC)).isFalse();

        assertThat(roleChecker.canSuperviseProblems(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(SUPERVISOR, contestAStarted)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(SUPERVISOR, contestB)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(SupervisorPermissionType.PROBLEM);
        assertThat(roleChecker.canSuperviseProblems(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(SUPERVISOR, contestAStarted)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(SUPERVISOR, contestB)).isTrue();
        assertThat(roleChecker.canSuperviseProblems(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(roleChecker.canSuperviseProblems(SUPERVISOR, contestBFinished)).isTrue();
        assertThat(roleChecker.canSuperviseProblems(SUPERVISOR, contestC)).isFalse();

        assertThat(roleChecker.canSuperviseProblems(MANAGER, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(MANAGER, contestAStarted)).isFalse();
        assertThat(roleChecker.canSuperviseProblems(MANAGER, contestB)).isTrue();
        assertThat(roleChecker.canSuperviseProblems(MANAGER, contestBStarted)).isTrue();
        assertThat(roleChecker.canSuperviseProblems(MANAGER, contestBFinished)).isTrue();
        assertThat(roleChecker.canSuperviseProblems(MANAGER, contestC)).isFalse();
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

        assertThat(roleChecker.canSubmitProblem(ADMIN, contestA, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(ADMIN, contestAStarted, contestantProblem)).isEmpty();
        assertThat(roleChecker.canSubmitProblem(ADMIN, contestB, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(ADMIN, contestBStarted, contestantProblem)).isEmpty();
        assertThat(roleChecker.canSubmitProblem(ADMIN, contestBFinished, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(ADMIN, contestC, contestantProblem)).isPresent();

        assertThat(roleChecker.canSubmitProblem(USER, contestA, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(USER, contestAStarted, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(USER, contestB, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(USER, contestBStarted, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(USER, contestBFinished, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(USER, contestC, contestantProblem)).isPresent();

        assertThat(roleChecker.canSubmitProblem(CONTESTANT, contestA, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(CONTESTANT, contestAStarted, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(CONTESTANT, contestB, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(CONTESTANT, contestBStarted, contestantProblem)).isEmpty();
        assertThat(roleChecker.canSubmitProblem(CONTESTANT, contestBFinished, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(CONTESTANT, contestC, contestantProblem)).isPresent();

        assertThat(roleChecker.canSubmitProblem(SUPERVISOR, contestA, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(SUPERVISOR, contestAStarted, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(SUPERVISOR, contestB, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(SUPERVISOR, contestBStarted, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(SUPERVISOR, contestBFinished, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(SUPERVISOR, contestC, contestantProblem)).isPresent();
        addSupervisorToContestBWithPermission(SupervisorPermissionType.PROBLEM);
        assertThat(roleChecker.canSubmitProblem(SUPERVISOR, contestA, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(SUPERVISOR, contestAStarted, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(SUPERVISOR, contestB, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(SUPERVISOR, contestBStarted, contestantProblem)).isEmpty();
        assertThat(roleChecker.canSubmitProblem(SUPERVISOR, contestBFinished, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(SUPERVISOR, contestC, contestantProblem)).isPresent();

        assertThat(roleChecker.canSubmitProblem(MANAGER, contestA, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(MANAGER, contestAStarted, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(MANAGER, contestB, contestantProblem)).isPresent();
        assertThat(roleChecker.canSubmitProblem(MANAGER, contestBStarted, contestantProblem)).isEmpty();
        assertThat(roleChecker.canSubmitProblem(MANAGER, contestC, contestantProblem)).isPresent();

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

        assertThat(roleChecker.canSubmitProblem(CONTESTANT, contestA, contestantProblem))
                .contains("You are not a contestant.");
        assertThat(roleChecker.canSubmitProblem(CONTESTANT, contestB, contestantProblem))
                .contains("Contest has not started yet.");
        assertThat(roleChecker.canSubmitProblem(CONTESTANT, contestBFinished, contestantProblem))
                .contains("Contest is over.");
        assertThat(roleChecker.canSubmitProblem(CONTESTANT, contestBStarted, contestantProblemClosed))
                .contains("Problem is closed.");
        assertThat(roleChecker.canSubmitProblem(CONTESTANT, contestBStarted, contestantProblemLimitReached))
                .contains("Submissions limit has been reached.");
        assertThat(roleChecker.canSubmitProblem(CONTESTANT, contestBStarted, contestantProblemNoLimit))
                .isEmpty();
    }

    @Test
    void view_default_scoreboard() {
        assertThat(roleChecker.canViewDefaultScoreboard(ADMIN, contestA)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(ADMIN, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(ADMIN, contestB)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(ADMIN, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(ADMIN, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(ADMIN, contestC)).isTrue();

        assertThat(roleChecker.canViewDefaultScoreboard(USER, contestA)).isFalse();
        assertThat(roleChecker.canViewDefaultScoreboard(USER, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(USER, contestB)).isFalse();
        assertThat(roleChecker.canViewDefaultScoreboard(USER, contestC)).isFalse();

        assertThat(roleChecker.canViewDefaultScoreboard(CONTESTANT, contestA)).isFalse();
        assertThat(roleChecker.canViewDefaultScoreboard(CONTESTANT, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(CONTESTANT, contestB)).isFalse();
        assertThat(roleChecker.canViewDefaultScoreboard(CONTESTANT, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(CONTESTANT, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(CONTESTANT, contestC)).isFalse();

        assertThat(roleChecker.canViewDefaultScoreboard(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canViewDefaultScoreboard(SUPERVISOR, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(SUPERVISOR, contestB)).isFalse();
        assertThat(roleChecker.canViewDefaultScoreboard(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(SUPERVISOR, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(SupervisorPermissionType.SCOREBOARD);
        assertThat(roleChecker.canViewDefaultScoreboard(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canViewDefaultScoreboard(SUPERVISOR, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(SUPERVISOR, contestB)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(SUPERVISOR, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(SUPERVISOR, contestC)).isFalse();

        assertThat(roleChecker.canViewDefaultScoreboard(MANAGER, contestA)).isFalse();
        assertThat(roleChecker.canViewDefaultScoreboard(MANAGER, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(MANAGER, contestB)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(MANAGER, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(MANAGER, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewDefaultScoreboard(MANAGER, contestC)).isFalse();
    }

    @Test
    void supervise_scoreboard() {
        assertThat(roleChecker.canSuperviseScoreboard(ADMIN, contestA)).isTrue();
        assertThat(roleChecker.canSuperviseScoreboard(ADMIN, contestAStarted)).isTrue();
        assertThat(roleChecker.canSuperviseScoreboard(ADMIN, contestB)).isTrue();
        assertThat(roleChecker.canSuperviseScoreboard(ADMIN, contestBStarted)).isTrue();
        assertThat(roleChecker.canSuperviseScoreboard(ADMIN, contestBFinished)).isTrue();
        assertThat(roleChecker.canSuperviseScoreboard(ADMIN, contestC)).isTrue();

        assertThat(roleChecker.canSuperviseScoreboard(USER, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(USER, contestAStarted)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(USER, contestB)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(USER, contestBStarted)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(USER, contestBFinished)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(USER, contestC)).isFalse();

        assertThat(roleChecker.canSuperviseScoreboard(CONTESTANT, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(CONTESTANT, contestAStarted)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(CONTESTANT, contestB)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(CONTESTANT, contestBStarted)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(CONTESTANT, contestC)).isFalse();

        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestAStarted)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestB)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(SupervisorPermissionType.SCOREBOARD);
        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestAStarted)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestB)).isTrue();
        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestBFinished)).isTrue();
        assertThat(roleChecker.canSuperviseScoreboard(SUPERVISOR, contestC)).isFalse();

        assertThat(roleChecker.canSuperviseScoreboard(MANAGER, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(MANAGER, contestAStarted)).isFalse();
        assertThat(roleChecker.canSuperviseScoreboard(MANAGER, contestB)).isTrue();
        assertThat(roleChecker.canSuperviseScoreboard(MANAGER, contestBStarted)).isTrue();
        assertThat(roleChecker.canSuperviseScoreboard(MANAGER, contestBFinished)).isTrue();
        assertThat(roleChecker.canSuperviseScoreboard(MANAGER, contestC)).isFalse();
    }

    @Test
    void view_submission() {
        assertThat(roleChecker.canViewSubmission(ADMIN, contestB, CONTESTANT)).isTrue();
        assertThat(roleChecker.canViewSubmission(ADMIN, contestBStarted, CONTESTANT)).isTrue();
        assertThat(roleChecker.canViewSubmission(ADMIN, contestBFinished, CONTESTANT)).isTrue();

        assertThat(roleChecker.canViewSubmission(CONTESTANT, contestB, CONTESTANT)).isTrue();
        assertThat(roleChecker.canViewSubmission(CONTESTANT, contestBStarted, CONTESTANT)).isTrue();
        assertThat(roleChecker.canViewSubmission(CONTESTANT, contestB, ANOTHER_CONTESTANT)).isFalse();
        assertThat(roleChecker.canViewSubmission(CONTESTANT, contestBStarted, ANOTHER_CONTESTANT)).isFalse();

        assertThat(roleChecker.canViewSubmission(SUPERVISOR, contestB, CONTESTANT)).isFalse();
        assertThat(roleChecker.canViewSubmission(SUPERVISOR, contestBStarted, CONTESTANT)).isFalse();
        addSupervisorToContestBWithPermission(SupervisorPermissionType.SUBMISSION);
        assertThat(roleChecker.canViewSubmission(SUPERVISOR, contestB, CONTESTANT)).isTrue();
        assertThat(roleChecker.canViewSubmission(SUPERVISOR, contestBStarted, CONTESTANT)).isTrue();
        assertThat(roleChecker.canViewSubmission(SUPERVISOR, contestBFinished, CONTESTANT)).isTrue();

        assertThat(roleChecker.canViewSubmission(MANAGER, contestB, CONTESTANT)).isTrue();
        assertThat(roleChecker.canViewSubmission(MANAGER, contestBStarted, CONTESTANT)).isTrue();
        assertThat(roleChecker.canViewSubmission(MANAGER, contestBFinished, CONTESTANT)).isTrue();
    }

    @Test
    void view_own_submissions() {
        assertThat(roleChecker.canViewOwnSubmissions(ADMIN, contestA)).isTrue();
        assertThat(roleChecker.canViewOwnSubmissions(ADMIN, contestAStarted)).isTrue();
        assertThat(roleChecker.canViewOwnSubmissions(ADMIN, contestB)).isTrue();
        assertThat(roleChecker.canViewOwnSubmissions(ADMIN, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewOwnSubmissions(ADMIN, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewOwnSubmissions(ADMIN, contestC)).isTrue();

        assertThat(roleChecker.canViewOwnSubmissions(USER, contestA)).isFalse();
        assertThat(roleChecker.canViewOwnSubmissions(USER, contestAStarted)).isFalse();
        assertThat(roleChecker.canViewOwnSubmissions(USER, contestB)).isFalse();
        assertThat(roleChecker.canViewOwnSubmissions(USER, contestBStarted)).isFalse();
        assertThat(roleChecker.canViewOwnSubmissions(USER, contestC)).isFalse();

        assertThat(roleChecker.canViewOwnSubmissions(CONTESTANT, contestA)).isFalse();
        assertThat(roleChecker.canViewOwnSubmissions(CONTESTANT, contestAStarted)).isFalse();
        assertThat(roleChecker.canViewOwnSubmissions(CONTESTANT, contestB)).isFalse();
        assertThat(roleChecker.canViewOwnSubmissions(CONTESTANT, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewOwnSubmissions(CONTESTANT, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewOwnSubmissions(CONTESTANT, contestC)).isFalse();

        assertThat(roleChecker.canViewOwnSubmissions(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canViewOwnSubmissions(SUPERVISOR, contestAStarted)).isFalse();
        assertThat(roleChecker.canViewOwnSubmissions(SUPERVISOR, contestB)).isFalse();
        assertThat(roleChecker.canViewOwnSubmissions(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewOwnSubmissions(SUPERVISOR, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewOwnSubmissions(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(SupervisorPermissionType.SUBMISSION);
        assertThat(roleChecker.canViewOwnSubmissions(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canViewOwnSubmissions(SUPERVISOR, contestAStarted)).isFalse();
        assertThat(roleChecker.canViewOwnSubmissions(SUPERVISOR, contestB)).isTrue();
        assertThat(roleChecker.canViewOwnSubmissions(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewOwnSubmissions(SUPERVISOR, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewOwnSubmissions(SUPERVISOR, contestC)).isFalse();

        assertThat(roleChecker.canViewOwnSubmissions(MANAGER, contestA)).isFalse();
        assertThat(roleChecker.canViewOwnSubmissions(MANAGER, contestAStarted)).isFalse();
        assertThat(roleChecker.canViewOwnSubmissions(MANAGER, contestB)).isTrue();
        assertThat(roleChecker.canViewOwnSubmissions(MANAGER, contestBStarted)).isTrue();
        assertThat(roleChecker.canViewOwnSubmissions(MANAGER, contestBFinished)).isTrue();
        assertThat(roleChecker.canViewOwnSubmissions(MANAGER, contestC)).isFalse();
    }

    @Test
    void supervise_submissions() {
        assertThat(roleChecker.canSuperviseSubmissions(ADMIN, contestA)).isTrue();
        assertThat(roleChecker.canSuperviseSubmissions(ADMIN, contestAStarted)).isTrue();
        assertThat(roleChecker.canSuperviseSubmissions(ADMIN, contestB)).isTrue();
        assertThat(roleChecker.canSuperviseSubmissions(ADMIN, contestBStarted)).isTrue();
        assertThat(roleChecker.canSuperviseSubmissions(ADMIN, contestC)).isTrue();

        assertThat(roleChecker.canSuperviseSubmissions(USER, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseSubmissions(USER, contestAStarted)).isFalse();
        assertThat(roleChecker.canSuperviseSubmissions(USER, contestB)).isFalse();
        assertThat(roleChecker.canSuperviseSubmissions(USER, contestBStarted)).isFalse();
        assertThat(roleChecker.canSuperviseSubmissions(USER, contestC)).isFalse();

        assertThat(roleChecker.canSuperviseSubmissions(CONTESTANT, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseSubmissions(CONTESTANT, contestAStarted)).isFalse();
        assertThat(roleChecker.canSuperviseSubmissions(CONTESTANT, contestB)).isFalse();
        assertThat(roleChecker.canSuperviseSubmissions(CONTESTANT, contestBStarted)).isFalse();
        assertThat(roleChecker.canSuperviseSubmissions(CONTESTANT, contestC)).isFalse();


        assertThat(roleChecker.canSuperviseSubmissions(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseSubmissions(SUPERVISOR, contestB)).isFalse();
        assertThat(roleChecker.canSuperviseSubmissions(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(roleChecker.canSuperviseSubmissions(SUPERVISOR, contestC)).isFalse();
        addSupervisorToContestBWithPermission(SupervisorPermissionType.SUBMISSION);
        assertThat(roleChecker.canSuperviseSubmissions(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseSubmissions(SUPERVISOR, contestB)).isTrue();
        assertThat(roleChecker.canSuperviseSubmissions(SUPERVISOR, contestBStarted)).isTrue();
        assertThat(roleChecker.canSuperviseSubmissions(SUPERVISOR, contestC)).isFalse();

        assertThat(roleChecker.canSuperviseSubmissions(MANAGER, contestA)).isFalse();
        assertThat(roleChecker.canSuperviseSubmissions(MANAGER, contestAStarted)).isFalse();
        assertThat(roleChecker.canSuperviseSubmissions(MANAGER, contestB)).isTrue();
        assertThat(roleChecker.canSuperviseSubmissions(MANAGER, contestBStarted)).isTrue();
        assertThat(roleChecker.canSuperviseSubmissions(MANAGER, contestC)).isFalse();
    }

    @Test
    void add_contestants() {
        assertThat(roleChecker.canAddContestants(ADMIN, contestA)).isTrue();
        assertThat(roleChecker.canAddContestants(ADMIN, contestB)).isTrue();
        assertThat(roleChecker.canAddContestants(ADMIN, contestC)).isTrue();

        assertThat(roleChecker.canAddContestants(USER, contestA)).isFalse();
        assertThat(roleChecker.canAddContestants(USER, contestB)).isFalse();
        assertThat(roleChecker.canAddContestants(USER, contestC)).isFalse();

        assertThat(roleChecker.canAddContestants(CONTESTANT, contestA)).isFalse();
        assertThat(roleChecker.canAddContestants(CONTESTANT, contestB)).isFalse();
        assertThat(roleChecker.canAddContestants(CONTESTANT, contestC)).isFalse();

        assertThat(roleChecker.canAddContestants(SUPERVISOR, contestA)).isFalse();
        assertThat(roleChecker.canAddContestants(SUPERVISOR, contestB)).isFalse();
        assertThat(roleChecker.canAddContestants(SUPERVISOR, contestC)).isFalse();

        assertThat(roleChecker.canAddContestants(MANAGER, contestA)).isFalse();
        assertThat(roleChecker.canAddContestants(MANAGER, contestB)).isTrue();
        assertThat(roleChecker.canAddContestants(MANAGER, contestC)).isFalse();
    }

    private void prepareContestA() {
        contestA = contestStore.createContest(new ContestData.Builder()
                .name("Contest A")
                .beginTime(NOW.plusMillis(1))
                .build());
        contestAStarted = contestStore.createContest(new ContestData.Builder()
                .name("Contest A - Started")
                .beginTime(NOW)
                .build());

        moduleStore.upsertRegistrationModule(contestA.getJid());
        moduleStore.upsertRegistrationModule(contestAStarted.getJid());
    }

    private void prepareContestB() {
        contestB = contestStore.createContest(new ContestData.Builder()
                .name("Contest B")
                .beginTime(NOW.plusMillis(1))
                .build());
        contestBStarted = contestStore.createContest(new ContestData.Builder()
                .name("Contest B - Started")
                .beginTime(NOW)
                .duration(Duration.ofHours(5))
                .build());
        contestBFinished = contestStore.createContest(new ContestData.Builder()
                .name("Contest B - Ended")
                .beginTime(NOW.minus(10, HOURS))
                .build());

        contestantStore.upsertContestant(contestB.getJid(), CONTESTANT);
        contestantStore.upsertContestant(contestBStarted.getJid(), CONTESTANT);
        contestantStore.upsertContestant(contestBStarted.getJid(), ANOTHER_CONTESTANT);
        contestantStore.upsertContestant(contestBFinished.getJid(), CONTESTANT);

        supervisorStore.upsertSupervisor(
                contestB.getJid(),
                new ContestSupervisorData.Builder()
                        .userJid(SUPERVISOR)
                        .permission(SupervisorPermission.of(ImmutableSet.of())).build());
        supervisorStore.upsertSupervisor(
                contestBStarted.getJid(),
                new ContestSupervisorData.Builder()
                        .userJid(SUPERVISOR)
                        .permission(SupervisorPermission.of(ImmutableSet.of())).build());
        supervisorStore.upsertSupervisor(
                contestBFinished.getJid(),
                new ContestSupervisorData.Builder()
                        .userJid(SUPERVISOR)
                        .permission(SupervisorPermission.of(ImmutableSet.of())).build());

        managerStore.upsertManager(contestB.getJid(), MANAGER);
        managerStore.upsertManager(contestBStarted.getJid(), MANAGER);
        managerStore.upsertManager(contestBFinished.getJid(), MANAGER);
    }

    private void addSupervisorToContestBWithPermission(SupervisorPermissionType type) {
        supervisorStore.upsertSupervisor(
                contestB.getJid(),
                new ContestSupervisorData.Builder()
                        .userJid(SUPERVISOR)
                        .permission(SupervisorPermission.of(ImmutableSet.of(type))).build());
        supervisorStore.upsertSupervisor(
                contestBStarted.getJid(),
                new ContestSupervisorData.Builder()
                        .userJid(SUPERVISOR)
                        .permission(SupervisorPermission.of(ImmutableSet.of(type))).build());
        supervisorStore.upsertSupervisor(
                contestBFinished.getJid(),
                new ContestSupervisorData.Builder()
                        .userJid(SUPERVISOR)
                        .permission(SupervisorPermission.of(ImmutableSet.of(type))).build());
    }

    private void prepareContestC() {
        contestC = contestStore.createContest(new ContestData.Builder()
                .name("Contest C")
                .beginTime(NOW.plusMillis(1))
                .build());
    }
}
