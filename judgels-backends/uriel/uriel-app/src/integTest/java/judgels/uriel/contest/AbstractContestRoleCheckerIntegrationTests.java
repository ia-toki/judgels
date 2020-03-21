package judgels.uriel.contest;

import static java.time.temporal.ChronoUnit.HOURS;
import static judgels.persistence.TestClock.NOW;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import judgels.jophiel.api.role.UserRole;
import judgels.persistence.TestClock;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.UrielCacheUtils;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import judgels.uriel.api.role.UrielRole;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.manager.ContestManagerStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModuleModel;
import judgels.uriel.persistence.ContestProblemModel;
import judgels.uriel.persistence.ContestScoreboardModel;
import judgels.uriel.persistence.ContestSupervisorModel;
import judgels.uriel.role.AbstractRoleCheckerIntegrationTests;
import judgels.uriel.role.RoleChecker;
import org.hibernate.SessionFactory;

@WithHibernateSession(models = {
        ContestModel.class,
        ContestModuleModel.class,
        ContestContestantModel.class,
        ContestProblemModel.class,
        ContestScoreboardModel.class,
        ContestSupervisorModel.class,
        ContestManagerModel.class})
public abstract class AbstractContestRoleCheckerIntegrationTests extends AbstractRoleCheckerIntegrationTests {
    protected static final String USER = "userJid";
    protected static final String ADMIN = "adminJid";
    protected static final String CONTESTANT = "contestantJid";
    protected static final String ANOTHER_CONTESTANT = "anotherContestantJid";
    protected static final String SUPERVISOR = "supervisorJid";
    protected static final String MANAGER = "managerJid";

    protected Contest contestA;
    protected Contest contestAStarted;
    protected Contest contestB;
    protected Contest contestBStarted;
    protected Contest contestBStartedPaused;
    protected Contest contestBFinished;
    protected Contest contestC;

    protected UrielIntegrationTestComponent component;

    protected RoleChecker roleChecker;
    protected ContestStore contestStore;
    protected ContestModuleStore moduleStore;
    protected ContestContestantStore contestantStore;
    protected ContestSupervisorStore supervisorStore;
    protected ContestManagerStore managerStore;

    protected AbstractContestRoleCheckerIntegrationTests() {
        UrielCacheUtils.removeDurations();
    }

    protected void prepare(SessionFactory sessionFactory) {
        component = createComponent(sessionFactory, new TestClock());

        roleChecker = component.roleChecker();
        contestStore = component.contestStore();
        moduleStore = component.contestModuleStore();
        contestantStore = component.contestContestantStore();
        supervisorStore = component.contestSupervisorStore();
        managerStore = component.contestManagerStore();

        prepareRoles();
        prepareContestA();
        prepareContestB();
        prepareContestC();
    }

    protected void prepareRoles() {
        setRoles(roleChecker, ImmutableMap.<String, UserRole>builder()
                .put(ADMIN, new UserRole.Builder().uriel(UrielRole.ADMIN.name()).build())
                .put(USER, new UserRole.Builder().build())
                .put(CONTESTANT, new UserRole.Builder().build())
                .put(ANOTHER_CONTESTANT, new UserRole.Builder().build())
                .put(SUPERVISOR, new UserRole.Builder().build())
                .put(MANAGER, new UserRole.Builder().build())
                .build());
    }

    protected void prepareContestA() {
        contestA = contestStore.createContest(new ContestCreateData.Builder().slug("contest-a").build());
        contestA = contestStore.updateContest(
                contestA.getJid(),
                new ContestUpdateData.Builder().beginTime(NOW.plusMillis(1)).build()).get();

        contestAStarted = contestStore.createContest(new ContestCreateData.Builder().slug("contest-a-started").build());
        contestAStarted = contestStore.updateContest(
                contestAStarted.getJid(),
                new ContestUpdateData.Builder().beginTime(NOW).build()).get();

        moduleStore.upsertRegistrationModule(contestA.getJid());
        moduleStore.upsertRegistrationModule(contestAStarted.getJid());
    }

    protected void prepareContestB() {
        contestB = contestStore.createContest(new ContestCreateData.Builder().slug("contest-b").build());
        contestB = contestStore.updateContest(
                contestB.getJid(),
                new ContestUpdateData.Builder().beginTime(NOW.plusMillis(1)).build()).get();

        contestBStarted = contestStore.createContest(new ContestCreateData.Builder().slug("contest-b-started").build());
        contestBStarted = contestStore.updateContest(
                contestBStarted.getJid(),
                new ContestUpdateData.Builder()
                        .beginTime(NOW.minus(2, HOURS))
                        .duration(Duration.ofHours(5))
                        .build()).get();

        contestBStartedPaused =
                contestStore.createContest(new ContestCreateData.Builder().slug("contest-b-started-paused").build());
        contestBStartedPaused = contestStore.updateContest(
                contestBStartedPaused.getJid(),
                new ContestUpdateData.Builder()
                        .beginTime(NOW.minus(2, HOURS))
                        .duration(Duration.ofHours(5))
                        .build()).get();

        contestBFinished = contestStore.createContest(
                new ContestCreateData.Builder().slug("contest-b-finished").build());
        contestBFinished = contestStore.updateContest(
                contestBFinished.getJid(),
                new ContestUpdateData.Builder().beginTime(NOW.minus(10, HOURS)).build()).get();

        moduleStore.upsertPausedModule(contestBStartedPaused.getJid());

        contestantStore.upsertContestant(contestB.getJid(), CONTESTANT);
        contestantStore.upsertContestant(contestBStarted.getJid(), CONTESTANT);
        contestantStore.upsertContestant(contestBStarted.getJid(), ANOTHER_CONTESTANT);
        contestantStore.upsertContestant(contestBStartedPaused.getJid(), CONTESTANT);
        contestantStore.upsertContestant(contestBStartedPaused.getJid(), ANOTHER_CONTESTANT);
        contestantStore.upsertContestant(contestBFinished.getJid(), CONTESTANT);

        supervisorStore.upsertSupervisor(contestB.getJid(), SUPERVISOR, ImmutableSet.of());
        supervisorStore.upsertSupervisor(contestBStarted.getJid(), SUPERVISOR, ImmutableSet.of());
        supervisorStore.upsertSupervisor(contestBStartedPaused.getJid(), SUPERVISOR, ImmutableSet.of());
        supervisorStore.upsertSupervisor(contestBFinished.getJid(), SUPERVISOR, ImmutableSet.of());

        managerStore.upsertManager(contestB.getJid(), MANAGER);
        managerStore.upsertManager(contestBStarted.getJid(), MANAGER);
        managerStore.upsertManager(contestBStartedPaused.getJid(), MANAGER);
        managerStore.upsertManager(contestBFinished.getJid(), MANAGER);
    }

    protected void prepareContestC() {
        contestC = contestStore.createContest(new ContestCreateData.Builder().slug("contest-c").build());
        contestC = contestStore.updateContest(
                contestC.getJid(),
                new ContestUpdateData.Builder().beginTime(NOW.plusMillis(1)).build()).get();
    }

    protected void addSupervisorToContestBWithPermission(SupervisorManagementPermission permission) {
        supervisorStore.upsertSupervisor(contestB.getJid(), SUPERVISOR, ImmutableSet.of(permission));
        supervisorStore.upsertSupervisor(contestBStarted.getJid(), SUPERVISOR, ImmutableSet.of(permission));
        supervisorStore.upsertSupervisor(contestBFinished.getJid(), SUPERVISOR, ImmutableSet.of(permission));
    }
}
