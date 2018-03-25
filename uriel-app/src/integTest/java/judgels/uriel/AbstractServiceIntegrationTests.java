package judgels.uriel;

import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;

import com.google.common.collect.ImmutableMap;
import com.palantir.remoting3.clients.UserAgent;
import com.palantir.websecurity.WebSecurityConfiguration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.testing.DropwizardTestSupport;
import judgels.service.jaxrs.JaxRsClients;
import judgels.uriel.jophiel.JophielConfiguration;
import org.h2.Driver;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractServiceIntegrationTests {
    public static final String URIEL_JDBC_SUFFIX = "uriel";
    private static DropwizardTestSupport<UrielApplicationConfiguration> support;

    @BeforeAll
    static void beforeAll() {
        DataSourceFactory dbConfig = new DataSourceFactory();
        dbConfig.setDriverClass(Driver.class.getName());
        dbConfig.setUrl("jdbc:h2:mem:./" + URIEL_JDBC_SUFFIX);
        dbConfig.setProperties(ImmutableMap.<String, String>builder()
                .put(DIALECT, H2Dialect.class.getName())
                .put(HBM2DDL_AUTO, "create")
                .put(GENERATE_STATISTICS, "false")
                .build());

        UrielApplicationConfiguration config = new UrielApplicationConfiguration(
                dbConfig,
                WebSecurityConfiguration.DEFAULT,
                new UrielConfiguration.Builder()
                        .jophielConfig(JophielConfiguration.DEFAULT)
                        .build());

        support = new DropwizardTestSupport<>(UrielApplication.class, config);
        support.before();
    }

    @AfterAll
    static void afterAll() {
        support.after();
    }

    protected static <T> T createService(Class<T> serviceClass) {
        return JaxRsClients.create(
                serviceClass,
                "http://localhost:" + support.getLocalPort(),
                UserAgent.of(UserAgent.Agent.of("test", UserAgent.Agent.DEFAULT_VERSION)));
    }
}
