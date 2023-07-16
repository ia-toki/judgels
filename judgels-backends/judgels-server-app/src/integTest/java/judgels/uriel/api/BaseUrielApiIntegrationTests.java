package judgels.uriel.api;

import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.DRIVER;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.URL;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.jophiel.UserRatingClient;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.jophiel.api.user.rating.UserRatingUpdateData;
import judgels.sandalphon.api.problem.Problem;
import judgels.uriel.ContestClient;
import judgels.uriel.ContestContestantClient;
import judgels.uriel.ContestManagerClient;
import judgels.uriel.ContestModuleClient;
import judgels.uriel.ContestProblemClient;
import judgels.uriel.ContestSupervisorClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
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

public abstract class BaseUrielApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
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

    protected static String managerToken;
    protected static String supervisorToken;
    protected static String supervisorAToken;
    protected static String supervisorBToken;
    protected static String contestantToken;
    protected static String contestantAToken;
    protected static String contestantBToken;
    protected static String userAToken;
    protected static String userBToken;

    protected static Problem problem1;
    protected static Problem problem2;
    protected static Problem problem3;

    protected ContestClient contestClient = createClient(ContestClient.class);
    protected ContestModuleClient moduleClient = createClient(ContestModuleClient.class);
    protected ContestManagerClient managerClient = createClient(ContestManagerClient.class);
    protected ContestSupervisorClient supervisorClient = createClient(ContestSupervisorClient.class);
    protected ContestContestantClient contestantClient = createClient(ContestContestantClient.class);
    protected ContestProblemClient problemClient = createClient(ContestProblemClient.class);

    @BeforeAll
    static void setUpUriel() {
        manager = createUser("manager");
        managerToken = getToken(manager);

        supervisor = createUser("supervisor");
        supervisorToken = getToken(supervisor);

        supervisorA = createUser("supervisorA");
        supervisorAToken = getToken(supervisorA);

        supervisorB = createUser("supervisorB");
        supervisorBToken = getToken(supervisorB);

        contestant = createUser("contestant");
        contestantToken = getToken(contestant);

        contestantA = createUser("contestantA");
        contestantAToken = getToken(contestantA);

        contestantB = createUser("contestantB");
        contestantBToken = getToken(contestantB);

        userA = createUser("userA");
        userAToken = getToken(userA);

        userB = createUser("userB");
        userBToken = getToken(userB);

        updateRatings(new UserRatingUpdateData.Builder()
                .time(Instant.now())
                .eventJid("some-contest-jid")
                .putRatingsMap(userA.getJid(), UserRating.of(2000, 2000))
                .putRatingsMap(userB.getJid(), UserRating.of(1000, 1000))
                .build());

        webTarget = createWebTarget();

        problem1 = createProblem(managerToken, PROBLEM_1_SLUG);
        problem2 = createProblem(managerToken, PROBLEM_2_SLUG);
        problem3 = createBundleProblem(managerToken, PROBLEM_3_SLUG);
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
        return contestClient.createContest(adminToken, new ContestCreateData.Builder()
                .slug(randomString())
                .build());
    }

    protected Contest createContest(String slug) {
        Contest contest = contestClient.createContest(adminToken, new ContestCreateData.Builder()
                .slug(slug)
                .build());
        beginContest(contest);
        return contest;
    }

    protected Contest beginContest(Contest contest) {
        return contestClient.updateContest(adminToken, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.now())
                .build());
    }

    protected Contest endContest(Contest contest) {
        return contestClient.updateContest(adminToken, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.now().minus(Duration.ofHours(3)))
                .duration(Duration.ofHours(3).minus(Duration.ofSeconds(1)))
                .build());
    }

    protected Contest createContestWithRoles() {
        return buildContestWithRoles()
                .build();
    }

    protected Contest enableModule(Contest contest, ContestModuleType type) {
        moduleClient.enableModule(adminToken, contest.getJid(), type);
        return contest;
    }

    protected Contest enableModule(Contest contest, ContestModuleType type, ContestModulesConfig config) {
        moduleClient.enableModule(adminToken, contest.getJid(), type);
        moduleClient.upsertConfig(adminToken, contest.getJid(), config);
        return contest;
    }

    protected Contest disableModule(Contest contest, ContestModuleType type) {
        moduleClient.disableModule(adminToken, contest.getJid(), type);
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

        Set<ContestModuleType> modules = Set.of();
        Set<String> managers = Set.of();
        Set<String> supervisors = Set.of();
        Map<String, SupervisorManagementPermission> supervisorsWithManagementPermissions = Collections.emptyMap();
        Set<String> contestants = Set.of();
        List<String> problems = List.of();

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
            this.modules = Set.of(types);
            return this;
        }

        public ContestBuilder managers(String... usernames) {
            this.managers = Set.of(usernames);
            return this;
        }

        public ContestBuilder supervisors(String... usernames) {
            this.supervisors = Set.of(usernames);
            return this;
        }

        public ContestBuilder supervisorWithManagementPermissions(
                String supervisor,
                SupervisorManagementPermission permission) {
            this.supervisorsWithManagementPermissions = Map.of(supervisor, permission);
            return this;
        }

        public ContestBuilder contestants(String... usernames) {
            this.contestants = Set.of(usernames);
            return this;
        }

        public ContestBuilder problems(String... aliasAndSlugs) {
            this.problems = List.of(aliasAndSlugs);
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
                contest = contestClient.updateContest(adminToken, contest.getJid(), data);
            }

            String tokenForManager = adminToken;

            if (!managers.isEmpty()) {
                managerClient.upsertManagers(adminToken, contest.getJid(), managers);
                tokenForManager = managerToken;
            }

            if (!supervisors.isEmpty()) {
                ContestSupervisorUpsertData data = new ContestSupervisorUpsertData.Builder()
                        .usernames(supervisors)
                        .build();
                supervisorClient.upsertSupervisors(tokenForManager, contest.getJid(), data);
            }

            if (!supervisorsWithManagementPermissions.isEmpty()) {
                for (String supervisor : supervisorsWithManagementPermissions.keySet()) {
                    ContestSupervisorUpsertData data = new ContestSupervisorUpsertData.Builder()
                            .addUsernames(supervisor)
                            .addManagementPermissions(supervisorsWithManagementPermissions.get(supervisor))
                            .build();
                    supervisorClient.upsertSupervisors(tokenForManager, contest.getJid(), data);
                }
            }

            if (!contestants.isEmpty()) {
                contestantClient.upsertContestants(tokenForManager, contest.getJid(), contestants);
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
                problemClient.setProblems(tokenForManager, contest.getJid(), data);
            }

            for (ContestModuleType module : modules) {
                moduleClient.enableModule(tokenForManager, contest.getJid(), module);
            }

            return contest;
        }
    }

    private static void updateRatings(UserRatingUpdateData data) {
        createClient(UserRatingClient.class).updateRatings(adminToken, data);
    }
}
