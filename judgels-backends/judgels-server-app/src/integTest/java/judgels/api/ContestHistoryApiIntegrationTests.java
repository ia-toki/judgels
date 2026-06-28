package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.DRIVER;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.URL;

import java.time.Instant;
import judgels.api.contest.Contest;
import judgels.api.contest.history.ContestHistoryResponse;
import judgels.api.contest.module.ContestModuleType;
import judgels.api.user.rating.UserRating;
import judgels.contest.ContestHistoryClient;
import org.h2.Driver;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tlx.api.user.rating.UserRatingUpdateData;
import tlx.user.UserRatingClient;

class ContestHistoryApiIntegrationTests extends BaseContestApiIntegrationTests {
    private final ContestHistoryClient historyClient = createClient(ContestHistoryClient.class);
    private final UserRatingClient userRatingClient = createClient(UserRatingClient.class);

    @BeforeAll
    static void beforeAll() {
        Session session = openSession();
        Transaction txn = session.beginTransaction();
        session.createNativeQuery("CREATE ALIAS IF NOT EXISTS UNIX_TIMESTAMP FOR \"judgels.persistence.h2.H2SqlFunctions.unixTimestamp\"").executeUpdate();
        session.createNativeQuery("CREATE ALIAS IF NOT EXISTS FROM_UNIXTIME FOR \"judgels.persistence.h2.H2SqlFunctions.fromUnixTime\"").executeUpdate();
        txn.commit();
        session.close();
    }

    @Test
    void get_public_history() {
        assertNotFound(() -> historyClient.getPublicHistory("nonexistent"));

        ContestHistoryResponse empty = historyClient.getPublicHistory(CONTESTANT);
        assertThat(empty.getData()).isEmpty();
        assertThat(empty.getContestsMap()).isEmpty();

        Contest contest = buildContest()
                .modules(ContestModuleType.REGISTRATION)
                .contestants(CONTESTANT)
                .ended()
                .build();
        setFinalRank(contest.getJid(), contestant.getJid(), 3);

        userRatingClient.updateRatings(adminToken, new UserRatingUpdateData.Builder()
                .time(Instant.ofEpochSecond(100))
                .eventJid(contest.getJid())
                .putRatingsMap(contestant.getJid(), UserRating.of(1500, 1400))
                .build());

        ContestHistoryResponse history = historyClient.getPublicHistory(CONTESTANT);
        assertThat(history.getData()).hasSize(1);
        assertThat(history.getData().get(0).getContestJid()).isEqualTo(contest.getJid());
        assertThat(history.getData().get(0).getRank()).isEqualTo(3);
        assertThat(history.getData().get(0).getRating()).contains(UserRating.of(1500, 1400));
        assertThat(history.getContestsMap()).containsKey(contest.getJid());
        assertThat(history.getContestsMap().get(contest.getJid()).getSlug()).isEqualTo(contest.getSlug());
    }

    private static void setFinalRank(String contestJid, String userJid, int rank) {
        Session session = openSession();
        Transaction txn = session.beginTransaction();
        session.createNativeQuery(
                "update uriel_contest_contestant set finalRank = :rank "
                        + "where contestJid = :contestJid and userJid = :userJid")
                .setParameter("rank", rank)
                .setParameter("contestJid", contestJid)
                .setParameter("userJid", userJid)
                .executeUpdate();
        txn.commit();
        session.close();
    }

    private static Session openSession() {
        Configuration config = new Configuration();
        config.setProperty(DIALECT, H2Dialect.class.getName());
        config.setProperty(DRIVER, Driver.class.getName());
        config.setProperty(URL, "jdbc:h2:mem:test");
        config.setProperty(GENERATE_STATISTICS, "false");

        return config.buildSessionFactory().openSession();
    }
}
