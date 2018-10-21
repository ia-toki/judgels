package judgels.uriel.contest.role;

import static java.time.temporal.ChronoUnit.HOURS;
import static judgels.persistence.TestClock.NOW;

import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielCacheUtils;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestUpdateData;
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
import org.hibernate.SessionFactory;

@WithHibernateSession(models = {
        AdminRoleModel.class,
        ContestModel.class,
        ContestModuleModel.class,
        ContestContestantModel.class,
        ContestSupervisorModel.class,
        ContestManagerModel.class})
public abstract class AbstractRoleCheckerIntegrationTests extends AbstractIntegrationTests {
    protected static final String USER = "userJid";
    protected static final String ADMIN = "adminJid";
    protected static final String CONTESTANT = "contestantJid";
    protected static final String ANOTHER_CONTESTANT = "anotherContestantJid";
    protected static final String SUPERVISOR = "supervisorJid";
    protected static final String MANAGER = "managerJid";
    protected static final String SUPERVISOR_CONTESTANT = "supervisorContestantJid";

    protected Contest contestA;
    protected Contest contestAStarted;
    protected Contest contestB;
    protected Contest contestBStarted;
    protected Contest contestBFinished;
    protected Contest contestC;

    protected UrielIntegrationTestComponent component;

    protected ContestStore contestStore;
    protected ContestModuleStore moduleStore;
    protected ContestContestantStore contestantStore;
    protected ContestSupervisorStore supervisorStore;
    protected ContestManagerStore managerStore;

    protected AbstractRoleCheckerIntegrationTests() {
        UrielCacheUtils.removeDurations();
    }

    protected void prepare(SessionFactory sessionFactory) {
        component = createComponent(sessionFactory);

        AdminRoleStore adminRoleStore = component.adminRoleStore();
        contestStore = component.contestStore();
        moduleStore = component.contestModuleStore();
        contestantStore = component.contestContestantStore();
        supervisorStore = component.contestSupervisorStore();
        managerStore = component.contestManagerStore();

        adminRoleStore.addAdmin(ADMIN);

        prepareContestA();
        prepareContestB();
        prepareContestC();
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

        contestBFinished = contestStore.createContest(
                new ContestCreateData.Builder().slug("contest-b-finished").build());
        contestBFinished = contestStore.updateContest(
                contestBFinished.getJid(),
                new ContestUpdateData.Builder().beginTime(NOW.minus(10, HOURS)).build()).get();

        contestantStore.upsertContestant(contestB.getJid(), CONTESTANT);
        contestantStore.upsertContestant(contestB.getJid(), SUPERVISOR_CONTESTANT);
        contestantStore.upsertContestant(contestBStarted.getJid(), CONTESTANT);
        contestantStore.upsertContestant(contestBStarted.getJid(), SUPERVISOR_CONTESTANT);
        contestantStore.upsertContestant(contestBStarted.getJid(), ANOTHER_CONTESTANT);
        contestantStore.upsertContestant(contestBFinished.getJid(), CONTESTANT);
        contestantStore.upsertContestant(contestBFinished.getJid(), SUPERVISOR_CONTESTANT);

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

        supervisorStore.upsertSupervisor(
                contestB.getJid(),
                new ContestSupervisorData.Builder()
                        .userJid(SUPERVISOR_CONTESTANT)
                        .permission(SupervisorPermission.of(ImmutableSet.of())).build());
        supervisorStore.upsertSupervisor(
                contestBStarted.getJid(),
                new ContestSupervisorData.Builder()
                        .userJid(SUPERVISOR_CONTESTANT)
                        .permission(SupervisorPermission.of(ImmutableSet.of())).build());
        supervisorStore.upsertSupervisor(
                contestBFinished.getJid(),
                new ContestSupervisorData.Builder()
                        .userJid(SUPERVISOR_CONTESTANT)
                        .permission(SupervisorPermission.of(ImmutableSet.of())).build());

        managerStore.upsertManager(contestB.getJid(), MANAGER);
        managerStore.upsertManager(contestBStarted.getJid(), MANAGER);
        managerStore.upsertManager(contestBFinished.getJid(), MANAGER);
    }

    protected void prepareContestC() {
        contestC = contestStore.createContest(new ContestCreateData.Builder().slug("contest-c").build());
        contestC = contestStore.updateContest(
                contestC.getJid(),
                new ContestUpdateData.Builder().beginTime(NOW.plusMillis(1)).build()).get();
    }

    protected void addSupervisorToContestBWithPermission(SupervisorPermissionType type) {
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

        supervisorStore.upsertSupervisor(
                contestB.getJid(),
                new ContestSupervisorData.Builder()
                        .userJid(SUPERVISOR_CONTESTANT)
                        .permission(SupervisorPermission.of(ImmutableSet.of(type))).build());
        supervisorStore.upsertSupervisor(
                contestBStarted.getJid(),
                new ContestSupervisorData.Builder()
                        .userJid(SUPERVISOR_CONTESTANT)
                        .permission(SupervisorPermission.of(ImmutableSet.of(type))).build());
        supervisorStore.upsertSupervisor(
                contestBFinished.getJid(),
                new ContestSupervisorData.Builder()
                        .userJid(SUPERVISOR_CONTESTANT)
                        .permission(SupervisorPermission.of(ImmutableSet.of(type))).build());
    }
}
