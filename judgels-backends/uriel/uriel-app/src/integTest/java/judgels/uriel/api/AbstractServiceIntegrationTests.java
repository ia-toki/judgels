package judgels.uriel.api;

import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import com.palantir.conjure.java.api.config.service.UserAgent;
import com.palantir.websecurity.WebSecurityConfiguration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.testing.DropwizardTestSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.ws.rs.client.WebTarget;
import judgels.service.jaxrs.JaxRsClients;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielApplication;
import judgels.uriel.UrielApplicationConfiguration;
import judgels.uriel.UrielConfiguration;
import judgels.uriel.file.FileConfiguration;
import judgels.uriel.gabriel.GabrielConfiguration;
import judgels.uriel.jophiel.JophielConfiguration;
import judgels.uriel.sandalphon.SandalphonConfiguration;
import judgels.uriel.sealtiel.SealtielConfiguration;
import judgels.uriel.submission.SubmissionConfiguration;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.h2.Driver;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractServiceIntegrationTests extends AbstractIntegrationTests {
    public static final String URIEL_JDBC_SUFFIX = "uriel";
    private static DropwizardTestSupport<UrielApplicationConfiguration> support;
    private static Path baseDataDir;

    @BeforeAll
    static void beforeAll() throws IOException {
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
                        .jophielConfig(JophielConfiguration.DEFAULT)
                        .sandalphonConfig(SandalphonConfiguration.DEFAULT)
                        .sealtielConfig(SealtielConfiguration.DEFAULT)
                        .gabrielConfig(GabrielConfiguration.DEFAULT)
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

    protected static WebTarget createWebTarget() {
        return JerseyClientBuilder.createClient()
                .register(MultiPartFeature.class)
                .target("http://localhost:" + support.getLocalPort());
    }

    protected static <T> T createService(Class<T> serviceClass) {
        return JaxRsClients.create(
                serviceClass,
                "http://localhost:" + support.getLocalPort(),
                UserAgent.of(UserAgent.Agent.of("test", UserAgent.Agent.DEFAULT_VERSION)));
    }
}
