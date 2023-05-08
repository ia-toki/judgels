package judgels.uriel.api;

import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.DRIVER;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.URL;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.BaseJudgelsServiceIntegrationTests;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.jophiel.api.user.rating.UserRatingService;
import judgels.jophiel.api.user.rating.UserRatingUpdateData;
import judgels.sandalphon.api.problem.Problem;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestService;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.api.contest.manager.ContestManagerService;
import judgels.uriel.api.contest.module.ContestModuleService;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemService;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.api.contest.supervisor.ContestSupervisorService;
import judgels.uriel.api.contest.supervisor.ContestSupervisorUpsertData;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.h2.Driver;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseUrielServiceIntegrationTests extends BaseJudgelsServiceIntegrationTests {
    protected static final String ADMIN = "admin";
    protected static final String MANAGER = "manager";
    protected static final String SUPERVISOR = "supervisor";
    protected static final String SUPERVISOR_A = "supervisorA";
    protected static final String SUPERVISOR_B = "supervisorB";
    protected static final String CONTESTANT = "contestant";
    protected static final String CONTESTANT_A = "contestantA";
    protected static final String CONTESTANT_B = "contestantB";
    protected static final String USER = "user";
    protected static final String USER_A = "userA";
    protected static final String USER_B = "userB";

    protected static final String PROBLEM_1_SLUG = "problem1Slug";
    protected static final String PROBLEM_2_SLUG = "problem2Slug";
    protected static final String PROBLEM_3_SLUG = "problem3Slug";

    protected static User manager;
    protected static User supervisor;
    protected static User supervisorA;
    protected static User supervisorB;
    protected static User contestant;
    protected static User contestantA;
    protected static User contestantB;
    protected static User userA;
    protected static User userB;

    protected static AuthHeader managerHeader;
    protected static AuthHeader supervisorHeader;
    protected static AuthHeader supervisorAHeader;
    protected static AuthHeader supervisorBHeader;
    protected static AuthHeader contestantHeader;
    protected static AuthHeader contestantAHeader;
    protected static AuthHeader contestantBHeader;
    protected static AuthHeader userAHeader;
    protected static AuthHeader userBHeader;

    protected static Problem problem1;
    protected static Problem problem2;
    protected static Problem problem3;

    protected ContestService contestService = createService(ContestService.class);
    protected ContestModuleService moduleService = createService(ContestModuleService.class);
    protected ContestManagerService managerService = createService(ContestManagerService.class);
    protected ContestSupervisorService supervisorService = createService(ContestSupervisorService.class);
    protected ContestContestantService contestantService = createService(ContestContestantService.class);
    protected ContestProblemService problemService = createService(ContestProblemService.class);

    @BeforeAll
    static void setUpUriel() {
        manager = createUser("manager");
        managerHeader = getHeader(manager);

        supervisor = createUser("supervisor");
        supervisorHeader = getHeader(supervisor);

        supervisorA = createUser("supervisorA");
        supervisorAHeader = getHeader(supervisorA);

        supervisorB = createUser("supervisorB");
        supervisorBHeader = getHeader(supervisorB);

        contestant = createUser("contestant");
        contestantHeader = getHeader(contestant);

        contestantA = createUser("contestantA");
        contestantAHeader = getHeader(contestantA);

        contestantB = createUser("contestantB");
        contestantBHeader = getHeader(contestantB);

        userA = createUser("userA");
        userAHeader = getHeader(userA);

        userB = createUser("userB");
        userBHeader = getHeader(userB);

        updateRatings(new UserRatingUpdateData.Builder()
                .time(Instant.now())
                .eventJid("some-contest-jid")
                .putRatingsMap(userA.getJid(), UserRating.of(2000, 2000))
                .putRatingsMap(userB.getJid(), UserRating.of(1000, 1000))
                .build());

        webTarget = createWebTarget();

        problem1 = createProblem(managerHeader, PROBLEM_1_SLUG);
        problem2 = createProblem(managerHeader, PROBLEM_2_SLUG);
        problem3 = createBundleProblem(managerHeader, PROBLEM_3_SLUG);
    }

    @AfterEach
    void afterEach() {
        Configuration config = new Configuration();
        config.setProperty(DIALECT, H2Dialect.class.getName());
        config.setProperty(DRIVER, Driver.class.getName());
        config.setProperty(URL, "jdbc:h2:mem:./judgels");
        config.setProperty(GENERATE_STATISTICS, "false");

        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction txn = session.beginTransaction();
        session.createNativeQuery("delete from uriel_contest").executeUpdate();
        txn.commit();
        session.close();
    }

    protected Contest createContest() {
        return contestService.createContest(adminHeader, new ContestCreateData.Builder()
                .slug(randomString())
                .build());
    }

    protected Contest createContest(String slug) {
        Contest contest = contestService.createContest(adminHeader, new ContestCreateData.Builder()
                .slug(slug)
                .build());
        beginContest(contest);
        return contest;
    }

    protected Contest beginContest(Contest contest) {
        return contestService.updateContest(adminHeader, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.now())
                .build());
    }

    protected Contest endContest(Contest contest) {
        return contestService.updateContest(adminHeader, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.now().minus(Duration.ofHours(3)))
                .duration(Duration.ofHours(3).minus(Duration.ofSeconds(1)))
                .build());
    }

    protected Contest createContestWithRoles() {
        return buildContestWithRoles()
                .build();
    }

    protected Contest enableModule(Contest contest, ContestModuleType type) {
        moduleService.enableModule(adminHeader, contest.getJid(), type);
        return contest;
    }

    protected Contest enableModule(Contest contest, ContestModuleType type, ContestModulesConfig config) {
        moduleService.enableModule(adminHeader, contest.getJid(), type);
        moduleService.upsertConfig(adminHeader, contest.getJid(), config);
        return contest;
    }

    protected Contest disableModule(Contest contest, ContestModuleType type) {
        moduleService.disableModule(adminHeader, contest.getJid(), type);
        return contest;
    }

    protected ContestBuilder buildContest() {
        return new ContestBuilder();
    }

    protected ContestBuilder buildContestWithRoles() {
        return new ContestBuilder()
                .managers(MANAGER)
                .supervisors(SUPERVISOR)
                .contestants(CONTESTANT);
    }

    protected class ContestBuilder {
        Optional<Instant> beginTime = Optional.empty();
        Optional<Duration> duration = Optional.empty();
        Optional<ContestStyle> style = Optional.empty();

        Set<ContestModuleType> modules = Collections.emptySet();
        Set<String> managers = Collections.emptySet();
        Set<String> supervisors = Collections.emptySet();
        Map<String, SupervisorManagementPermission> supervisorsWithManagementPermissions = Collections.emptyMap();
        Set<String> contestants = Collections.emptySet();
        List<String> problems = Collections.emptyList();

        public ContestBuilder beginTime(Instant instant) {
            this.beginTime = Optional.of(instant);
            return this;
        }

        public ContestBuilder duration(Duration duration) {
            this.duration = Optional.of(duration);
            return this;
        }

        public ContestBuilder style(ContestStyle style) {
            this.style = Optional.of(style);
            return this;
        }

        public ContestBuilder begun() {
            this.beginTime = Optional.of(Instant.now().minus(Duration.ofSeconds(1)));
            return this;
        }

        public ContestBuilder ended() {
            this.beginTime = Optional.of(Instant.now().minus(Duration.ofHours(10)));
            return this;
        }

        public ContestBuilder modules(ContestModuleType... types) {
            this.modules = ImmutableSet.copyOf(types);
            return this;
        }

        public ContestBuilder managers(String... usernames) {
            this.managers = ImmutableSet.copyOf(usernames);
            return this;
        }

        public ContestBuilder supervisors(String... usernames) {
            this.supervisors = ImmutableSet.copyOf(usernames);
            return this;
        }

        public ContestBuilder supervisorWithManagementPermissions(
                String supervisor,
                SupervisorManagementPermission permission) {
            this.supervisorsWithManagementPermissions = ImmutableMap.of(supervisor, permission);
            return this;
        }

        public ContestBuilder contestants(String... usernames) {
            this.contestants = ImmutableSet.copyOf(usernames);
            return this;
        }

        public ContestBuilder problems(String... aliasAndSlugs) {
            this.problems = ImmutableList.copyOf(aliasAndSlugs);
            return this;
        }

        public Contest build() {
            Contest contest = createContest();

            if (beginTime.isPresent() || duration.isPresent() || style.isPresent()) {
                ContestUpdateData data = new ContestUpdateData.Builder()
                        .style(style)
                        .beginTime(beginTime)
                        .duration(duration)
                        .build();
                contest = contestService.updateContest(adminHeader, contest.getJid(), data);
            }

            AuthHeader headerForManager = adminHeader;

            if (!managers.isEmpty()) {
                managerService.upsertManagers(adminHeader, contest.getJid(), managers);
                headerForManager = managerHeader;
            }

            if (!supervisors.isEmpty()) {
                ContestSupervisorUpsertData data = new ContestSupervisorUpsertData.Builder()
                        .usernames(supervisors)
                        .build();
                supervisorService.upsertSupervisors(headerForManager, contest.getJid(), data);
            }

            if (!supervisorsWithManagementPermissions.isEmpty()) {
                for (String supervisor : supervisorsWithManagementPermissions.keySet()) {
                    ContestSupervisorUpsertData data = new ContestSupervisorUpsertData.Builder()
                            .addUsernames(supervisor)
                            .addManagementPermissions(supervisorsWithManagementPermissions.get(supervisor))
                            .build();
                    supervisorService.upsertSupervisors(headerForManager, contest.getJid(), data);
                }
            }

            if (!contestants.isEmpty()) {
                contestantService.upsertContestants(headerForManager, contest.getJid(), contestants);
            }

            if (!problems.isEmpty()) {
                List<ContestProblemData> data = new ArrayList<>();
                for (int i = 0; i < problems.size(); i += 2) {
                    data.add(new ContestProblemData.Builder()
                            .alias(problems.get(i))
                            .slug(problems.get(i + 1))
                            .status(ContestProblemStatus.OPEN)
                            .build());
                }
                problemService.setProblems(headerForManager, contest.getJid(), data);
            }

            for (ContestModuleType module : modules) {
                moduleService.enableModule(headerForManager, contest.getJid(), module);
            }

            return contest;
        }
    }

    private static void updateRatings(UserRatingUpdateData data) {
        createService(UserRatingService.class).updateRatings(adminHeader, data);
    }
}
