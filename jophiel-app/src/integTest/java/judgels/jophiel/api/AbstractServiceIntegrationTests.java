package judgels.jophiel.api;

import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;

import com.google.common.collect.ImmutableMap;
import com.palantir.remoting.api.config.service.ServiceConfiguration;
import com.palantir.remoting.api.config.ssl.SslConfiguration;
import com.palantir.remoting3.clients.ClientConfiguration;
import com.palantir.remoting3.clients.ClientConfigurations;
import com.palantir.remoting3.clients.UserAgent;
import com.palantir.remoting3.jaxrs.JaxRsClient;
import com.palantir.websecurity.WebSecurityConfiguration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.testing.DropwizardTestSupport;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import judgels.jophiel.JophielApplication;
import judgels.jophiel.JophielApplicationConfiguration;
import judgels.jophiel.JophielConfiguration;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.SessionService;
import judgels.jophiel.mailer.MailerConfiguration;
import judgels.service.api.actor.AuthHeader;
import org.h2.Driver;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractServiceIntegrationTests {
    private static DropwizardTestSupport<JophielApplicationConfiguration> support;

    protected static AuthHeader adminHeader;

    @BeforeAll public static void beforeAll() {
        DataSourceFactory dbConfig = new DataSourceFactory();
        dbConfig.setDriverClass(Driver.class.getName());
        dbConfig.setUrl("jdbc:h2:mem:./" + UUID.randomUUID().toString());
        dbConfig.setProperties(ImmutableMap.<String, String>builder()
                .put(DIALECT, H2Dialect.class.getName())
                .put(HBM2DDL_AUTO, "create")
                .put(GENERATE_STATISTICS, "false")
                .build());

        JophielConfiguration jophielConfig = new JophielConfiguration.Builder()
                .addMasterUsers("admin")
                .mailerConfig(new MailerConfiguration.Builder()
                        .host("localhost")
                        .port(2500)
                        .useSsl(false)
                        .username("wiser")
                        .password("wiser")
                        .sender("noreply@wiser.com")
                        .build())
                .build();

        JophielApplicationConfiguration config = new JophielApplicationConfiguration(
                dbConfig,
                WebSecurityConfiguration.DEFAULT,
                jophielConfig);

        support = new DropwizardTestSupport<>(JophielApplication.class, config);
        support.before();

        adminHeader = AuthHeader.of(createService(SessionService.class)
                .logIn(Credentials.of("admin", "master"))
                .getToken());
    }

    @AfterAll public static void afterAll() {
        support.after();
    }

    protected static <T> T createService(Class<T> serviceClass) {
        Path testTrustStore = Paths.get(
                AbstractServiceIntegrationTests.class.getClassLoader().getResource("test.jks").getPath());
        ServiceConfiguration serviceConfig = ServiceConfiguration.builder()
                .addUris("http://localhost:" + support.getLocalPort())
                .security(SslConfiguration.of(testTrustStore))
                .build();

        UserAgent userAgent = UserAgent.of(UserAgent.Agent.of("test", UserAgent.Agent.DEFAULT_VERSION));
        ClientConfiguration clientConfig = ClientConfigurations.of(serviceConfig);
        return JaxRsClient.create(serviceClass, userAgent, clientConfig);
    }
}
