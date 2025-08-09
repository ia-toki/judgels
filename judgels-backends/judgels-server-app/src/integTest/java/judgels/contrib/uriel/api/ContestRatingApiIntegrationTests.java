package judgels.contrib.uriel.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.DRIVER;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.URL;

import judgels.contrib.uriel.ContestRatingClient;
import judgels.uriel.api.BaseUrielApiIntegrationTests;
import org.h2.Driver;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ContestRatingApiIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestRatingClient ratingClient = createClient(ContestRatingClient.class);

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
    void get_pending_ratings() {
        assertThat(ratingClient.getContestsPendingRating(superadminToken).getData())
                .isEmpty();
    }
}
