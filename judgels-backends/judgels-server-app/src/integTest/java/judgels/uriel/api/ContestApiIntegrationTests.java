package judgels.uriel.api;

import static judgels.uriel.api.contest.ContestErrors.SLUG_ALREADY_EXISTS;
import static judgels.uriel.api.contest.module.ContestModuleType.HIDDEN;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.DRIVER;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.URL;

import com.google.common.collect.Lists;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestDescription;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.role.ContestRole;
import org.h2.Driver;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ContestApiIntegrationTests extends BaseUrielApiIntegrationTests {
    @BeforeAll
    static void beforeAll() {
        Configuration config = new Configuration();
        config.setProperty(DIALECT, H2Dialect.class.getName());
        config.setProperty(DRIVER, Driver.class.getName());
        config.setProperty(URL, "jdbc:h2:mem:test");
        config.setProperty(GENERATE_STATISTICS, "false");

        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction txn = session.beginTransaction();
        session.createNativeQuery("CREATE ALIAS IF NOT EXISTS UNIX_TIMESTAMP FOR \"judgels.persistence.h2.H2SqlFunctions.unixTimestamp\"").executeUpdate();
        session.createNativeQuery("CREATE ALIAS IF NOT EXISTS FROM_UNIXTIME FOR \"judgels.persistence.h2.H2SqlFunctions.fromUnixTime\"").executeUpdate();
        txn.commit();
        session.close();
    }

    @Test
    void create_update_get_contest() {
        assertNotFound(() -> contestClient.getContest(adminToken, "bogus"));
        assertNotFound(() -> contestClient.getContestBySlug(adminToken, "bogus"));
        assertNotFound(() -> contestClient.getContestDescription(adminToken, "bogus"));

        Contest contest = contestClient.createContest(adminToken, new ContestCreateData.Builder()
                .slug("contest")
                .build());

        assertThat(contest.getSlug()).isEqualTo("contest");
        assertThat(contest.getName()).isEqualTo("contest");
        assertThat(contest.getStyle()).isEqualTo(ContestStyle.ICPC);
        assertThat(contest.getBeginTime()).isAfter(Instant.now());
        assertThat(contestClient.getContest(adminToken, contest.getJid())).isEqualTo(contest);
        assertThat(contestClient.getContestBySlug(adminToken, contest.getSlug())).isEqualTo(contest);

        contest = contestClient.updateContest(adminToken, contest.getJid(), new ContestUpdateData.Builder()
                .name("Judgels Open Contest")
                .slug("contest-new")
                .style(ContestStyle.IOI)
                .beginTime(Instant.ofEpochSecond(42))
                .duration(Duration.ofHours(5))
                .build());

        assertThat(contest.getSlug()).isEqualTo("contest-new");
        assertThat(contest.getName()).isEqualTo("Judgels Open Contest");
        assertThat(contest.getStyle()).isEqualTo(ContestStyle.IOI);
        assertThat(contest.getBeginTime()).isEqualTo(Instant.ofEpochSecond(42));
        assertThat(contest.getDuration()).isEqualTo(Duration.ofHours(5));
        assertThat(contestClient.getContest(adminToken, contest.getJid())).isEqualTo(contest);
    }

    @Test
    void update_get_contest_description() {
        assertNotFound(() -> contestClient.getContestDescription(adminToken, "bogus"));

        Contest contest = createContest();

        ContestDescription description = contestClient.getContestDescription(adminToken, contest.getJid());
        assertThat(description.getDescription()).isEmpty();

        description = contestClient.updateContestDescription(
                adminToken,
                contest.getJid(),
                new ContestDescription.Builder()
                        .description("This is open contest")
                        .build());

        assertThat(description.getDescription()).contains("This is open contest");
        assertThat(contestClient.getContestDescription(adminToken, contest.getJid())).isEqualTo(description);
    }

    @Test
    void create_contest__bad_request() {
        createContest("contest");
        assertBadRequest(() -> contestClient.createContest(adminToken, new ContestCreateData.Builder()
                .slug("contest")
                .build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);
    }

    @Test
    void update_contest__bad_request() {
        createContest("contest-a");
        Contest contestB = createContest("contest-b");
        assertBadRequest(() ->
                contestClient.updateContest(adminToken, contestB.getJid(), new ContestUpdateData.Builder()
                        .slug("contest-a")
                        .build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);
    }

    @Test
    void get_contests_and_active_contests() {
        // contests will start in the future, considered as active

        Contest contestA = buildContest()
                .beginTime(Instant.now().plus(Duration.ofSeconds(10)))
                .managers(MANAGER)
                .contestants(CONTESTANT_A, USER)
                .build();
        Contest contestB = buildContest()
                .beginTime(Instant.now().plus(Duration.ofSeconds(9)))
                .managers(MANAGER)
                .supervisors(SUPERVISOR, USER)
                .contestants(CONTESTANT_A, CONTESTANT_B)
                .build();
        Contest contestC = buildContest()
                .beginTime(Instant.now().plus(Duration.ofSeconds(8)))
                .modules(HIDDEN)
                .managers(MANAGER, USER)
                .supervisors(SUPERVISOR)
                .contestants(CONTESTANT_A, CONTESTANT_B, USER)
                .build();
        Contest contestD = buildContest()
                .beginTime(Instant.now().plus(Duration.ofSeconds(7)))
                .modules(REGISTRATION)
                .managers(MANAGER)
                .supervisors(SUPERVISOR)
                .build();
        Contest contestE = buildContest()
                .beginTime(Instant.now().plus(Duration.ofSeconds(6)))
                .build();

        Map<String, List<Contest>> contestsMap = new LinkedHashMap<>();
        contestsMap.put(adminToken, List.of(contestA, contestB, contestC, contestD, contestE));
        contestsMap.put(managerToken, List.of(contestA, contestB, contestC, contestD));
        contestsMap.put(supervisorToken, List.of(contestB, contestD));
        contestsMap.put(contestantAToken, List.of(contestA, contestB, contestD));
        contestsMap.put(contestantBToken, List.of(contestB, contestD));
        contestsMap.put("", List.of(contestD));

        for (String token : contestsMap.keySet()) {
            var response = contestClient.getContests(token);
            assertThat(response.getData().getPage()).containsExactlyElementsOf(contestsMap.get(token));
            assertThat(response.getConfig().getCanAdminister()).isEqualTo(token.equals(adminToken));

            var activeResponse = contestClient.getActiveContests(token);
            assertThat(activeResponse.getData()).containsExactlyElementsOf(Lists.reverse(contestsMap.get(token)));
        }

        var response = contestClient.getContests(userToken);
        assertThat(response.getRolesMap()).isEqualTo(Map.of(
                contestA.getJid(), ContestRole.CONTESTANT,
                contestB.getJid(), ContestRole.SUPERVISOR,
                contestC.getJid(), ContestRole.MANAGER,
                contestD.getJid(), ContestRole.NONE));

        var activeResponse = contestClient.getActiveContests(userToken);
        assertThat(activeResponse.getRolesMap()).isEqualTo(response.getRolesMap());

        response = contestClient.getContests(adminToken);
        assertThat(response.getRolesMap()).isEqualTo(Map.of(
                contestA.getJid(), ContestRole.ADMIN,
                contestB.getJid(), ContestRole.ADMIN,
                contestC.getJid(), ContestRole.ADMIN,
                contestD.getJid(), ContestRole.ADMIN,
                contestE.getJid(), ContestRole.ADMIN));

        activeResponse = contestClient.getActiveContests(adminToken);
        assertThat(activeResponse.getRolesMap()).isEqualTo(response.getRolesMap());

        // contests are now running, considered as active

        contestA = beginContest(contestA);
        contestB = beginContest(contestB);
        contestC = beginContest(contestC);
        contestD = beginContest(contestD);
        contestE = beginContest(contestE);

        contestsMap.put(adminToken, List.of(contestA, contestB, contestC, contestD, contestE));
        contestsMap.put(managerToken, List.of(contestA, contestB, contestC, contestD));
        contestsMap.put(supervisorToken, List.of(contestB, contestD));
        contestsMap.put(contestantAToken, List.of(contestA, contestB, contestD));
        contestsMap.put(contestantBToken, List.of(contestB, contestD));
        contestsMap.put("", List.of(contestD));

        for (String token : contestsMap.keySet()) {
            activeResponse = contestClient.getActiveContests(token);
            assertThat(activeResponse.getData()).containsExactlyElementsOf(contestsMap.get(token));
        }

        // contests have ended, considered as inactive

        endContest(contestA);
        endContest(contestB);
        endContest(contestC);
        endContest(contestD);
        endContest(contestE);

        for (String token : contestsMap.keySet()) {
            activeResponse = contestClient.getActiveContests(token);
            assertThat(activeResponse.getData()).isEmpty();
        }
    }
}
