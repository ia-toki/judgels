package judgels.jerahmeel.api;

import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import com.palantir.websecurity.WebSecurityConfiguration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.testing.DropwizardTestSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.ws.rs.client.WebTarget;
import judgels.gabriel.api.GabrielClientConfiguration;
import judgels.jerahmeel.AbstractIntegrationTests;
import judgels.jerahmeel.JerahmeelApplication;
import judgels.jerahmeel.JerahmeelApplicationConfiguration;
import judgels.jerahmeel.JerahmeelConfiguration;
import judgels.jerahmeel.stats.StatsConfiguration;
import judgels.jerahmeel.submission.programming.SubmissionConfiguration;
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.messaging.rabbitmq.RabbitMQConfiguration;
import judgels.sandalphon.api.SandalphonClientConfiguration;
import judgels.service.jaxrs.JaxRsClients;
import judgels.uriel.api.UrielClientConfiguration;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.h2.Driver;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractServiceIntegrationTests extends AbstractIntegrationTests {
    public static final String JERAHMEEL_JDBC_SUFFIX = "jerahmeel";
    private static DropwizardTestSupport<JerahmeelApplicationConfiguration> support;
    private static Path baseDataDir;

    @BeforeAll
    static void beforeAll() throws Exception {
        DataSourceFactory dbConfig = new DataSourceFactory();
        dbConfig.setDriverClass(Driver.class.getName());
        dbConfig.setUrl("jdbc:h2:mem:./" + JERAHMEEL_JDBC_SUFFIX);
        dbConfig.setProperties(ImmutableMap.<String, String>builder()
                .put(DIALECT, H2Dialect.class.getName())
                .put(HBM2DDL_AUTO, "create")
                .put(GENERATE_STATISTICS, "false")
                .build());

        baseDataDir = Files.createTempDirectory("jerahmeel");

        JerahmeelApplicationConfiguration config = new JerahmeelApplicationConfiguration(
                dbConfig,
                WebSecurityConfiguration.DEFAULT,
                new JerahmeelConfiguration.Builder()
                        .baseDataDir(baseDataDir.toString())
                        .jophielConfig(JophielClientConfiguration.DEFAULT)
                        .sandalphonConfig(SandalphonClientConfiguration.DEFAULT)
                        .urielConfig(UrielClientConfiguration.DEFAULT)
                        .gabrielConfig(GabrielClientConfiguration.DEFAULT)
                        .rabbitMQConfig(RabbitMQConfiguration.DEFAULT)
                        .submissionConfig(SubmissionConfiguration.DEFAULT)
                        .statsConfig(StatsConfiguration.DEFAULT)
                        .build());

        support = new DropwizardTestSupport<>(JerahmeelApplication.class, config);
        support.before();
    }

    @AfterAll
    static void afterAll() throws IOException {
        support.after();
        MoreFiles.deleteRecursively(baseDataDir, RecursiveDeleteOption.ALLOW_INSECURE);
    }

    protected static WebTarget createWebTarget() {
        return JerseyClientBuilder.createClient()
                .register(MultiPartFeature.class)
                .target("http://localhost:" + support.getLocalPort());
    }

    protected static <T> T createService(Class<T> serviceClass) {
        return JaxRsClients.create(
                serviceClass,
                "http://localhost:" + support.getLocalPort());
    }
}
