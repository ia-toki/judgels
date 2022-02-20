package judgels.uriel.api;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.DRIVER;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.URL;

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
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.messaging.rabbitmq.RabbitMQConfiguration;
import judgels.sandalphon.api.SandalphonClientConfiguration;
import judgels.service.jaxrs.JaxRsClients;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielApplication;
import judgels.uriel.UrielApplicationConfiguration;
import judgels.uriel.UrielConfiguration;
import judgels.uriel.file.FileConfiguration;
import judgels.uriel.submission.programming.SubmissionConfiguration;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.h2.Driver;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractServiceIntegrationTests extends AbstractIntegrationTests {
    public static final String URIEL_JDBC_SUFFIX = "uriel";
    private static DropwizardTestSupport<UrielApplicationConfiguration> support;
    private static Path baseDataDir;

    @BeforeAll
    static void beforeAll() throws Exception {
        DataSourceFactory dbConfig = new DataSourceFactory();
        dbConfig.setDriverClass(Driver.class.getName());
        dbConfig.setUrl("jdbc:h2:mem:./" + URIEL_JDBC_SUFFIX);
        dbConfig.setProperties(ImmutableMap.<String, String>builder()
                .put(DIALECT, H2Dialect.class.getName())
                .put(HBM2DDL_AUTO, "create")
                .put(GENERATE_STATISTICS, "false")
                .build());

        baseDataDir = Files.createTempDirectory("uriel");

        UrielApplicationConfiguration config = new UrielApplicationConfiguration(
                dbConfig,
                WebSecurityConfiguration.DEFAULT,
                new UrielConfiguration.Builder()
                        .baseDataDir(baseDataDir.toString())
                        .jophielConfig(JophielClientConfiguration.DEFAULT)
                        .sandalphonConfig(SandalphonClientConfiguration.DEFAULT)
                        .gabrielConfig(GabrielClientConfiguration.DEFAULT)
                        .rabbitMQConfig(RabbitMQConfiguration.DEFAULT)
                        .submissionConfig(SubmissionConfiguration.DEFAULT)
                        .fileConfig(FileConfiguration.DEFAULT)
                        .build());

        support = new DropwizardTestSupport<>(UrielApplication.class, config);
        support.before();
    }

    @AfterAll
    static void afterAll() throws IOException {
        support.after();
        MoreFiles.deleteRecursively(baseDataDir, RecursiveDeleteOption.ALLOW_INSECURE);
    }

    @AfterEach
    void afterEach() {
        Configuration config = new Configuration();
        config.setProperty(DIALECT, H2Dialect.class.getName());
        config.setProperty(DRIVER, Driver.class.getName());
        config.setProperty(URL, "jdbc:h2:mem:./" + URIEL_JDBC_SUFFIX);
        config.setProperty(GENERATE_STATISTICS, "false");

        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction txn = session.beginTransaction();
        session.createNativeQuery("delete from uriel_contest").executeUpdate();
        txn.commit();
        session.close();
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

    protected static void assertPermitted(ThrowingCallable callable) {
        assertThatCode(callable).doesNotThrowAnyException();
    }

    protected static AbstractThrowableAssert<?, ? extends Throwable> assertBadRequest(ThrowingCallable callable) {
        return assertThatThrownBy(callable).hasFieldOrPropertyWithValue("code", 400);
    }

    protected static AbstractThrowableAssert<?, ? extends Throwable> assertForbidden(ThrowingCallable callable) {
        return assertThatThrownBy(callable).hasFieldOrPropertyWithValue("code", 403);
    }

    protected static void assertNotFound(ThrowingCallable callable) {
        assertThatThrownBy(callable).hasFieldOrPropertyWithValue("code", 404);
    }

    protected ThrowingCallable callAll(ThrowingCallable... callables) {
        return () -> {
            Throwable throwable = null;
            int throwables = 0;

            for (ThrowingCallable callable : callables) {
                try {
                    callable.call();
                } catch (Throwable t) {
                    throwables++;
                    throwable = t;
                }
            }

            if (throwables != 0 && throwables != callables.length) {
                throw new IllegalStateException();
            }

            if (throwable != null) {
                throw throwable;
            }
        };
    }

    protected static String randomString() {
        return "string" + (Math.random() * 1000000000);
    }
}
