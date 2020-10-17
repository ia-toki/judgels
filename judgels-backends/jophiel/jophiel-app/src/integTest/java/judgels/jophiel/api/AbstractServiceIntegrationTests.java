package judgels.jophiel.api;

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
import java.util.UUID;
import judgels.jophiel.JophielApplication;
import judgels.jophiel.JophielApplicationConfiguration;
import judgels.jophiel.JophielConfiguration;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.SessionService;
import judgels.jophiel.mailer.MailerConfiguration;
import judgels.jophiel.session.SessionConfiguration;
import judgels.jophiel.user.account.UserRegistrationConfiguration;
import judgels.jophiel.user.account.UserResetPasswordConfiguration;
import judgels.jophiel.user.avatar.UserAvatarConfiguration;
import judgels.jophiel.user.superadmin.SuperadminCreatorConfiguration;
import judgels.jophiel.web.WebConfiguration;
import judgels.service.api.actor.AuthHeader;
import judgels.service.jaxrs.JaxRsClients;
import org.h2.Driver;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractServiceIntegrationTests {
    private static DropwizardTestSupport<JophielApplicationConfiguration> support;
    private static Path baseDataDir;

    protected static AuthHeader adminHeader;

    @BeforeAll
    static void beforeAll() throws Exception {
        DataSourceFactory dbConfig = new DataSourceFactory();
        dbConfig.setDriverClass(Driver.class.getName());
        dbConfig.setUrl("jdbc:h2:mem:./" + UUID.randomUUID().toString());
        dbConfig.setProperties(ImmutableMap.<String, String>builder()
                .put(DIALECT, H2Dialect.class.getName())
                .put(HBM2DDL_AUTO, "create")
                .put(GENERATE_STATISTICS, "false")
                .build());

        baseDataDir = Files.createTempDirectory("jophiel");

        JophielConfiguration jophielConfig = new JophielConfiguration.Builder()
                .baseDataDir(baseDataDir.toString())
                .mailerConfig(new MailerConfiguration.Builder()
                        .host("localhost")
                        .port(2500)
                        .useSsl(false)
                        .username("wiser")
                        .password("wiser")
                        .sender("noreply@wiser.com")
                        .build())
                .userRegistrationConfig(UserRegistrationConfiguration.DEFAULT)
                .userResetPasswordConfig(UserResetPasswordConfiguration.DEFAULT)
                .userAvatarConfig(UserAvatarConfiguration.DEFAULT)
                .superadminCreatorConfig(SuperadminCreatorConfiguration.DEFAULT)
                .sessionConfig(SessionConfiguration.DEFAULT)
                .webConfig(WebConfiguration.DEFAULT)
                .build();

        JophielApplicationConfiguration config = new JophielApplicationConfiguration(
                dbConfig,
                WebSecurityConfiguration.DEFAULT,
                jophielConfig);

        support = new DropwizardTestSupport<>(JophielApplication.class, config);
        support.before();

        adminHeader = AuthHeader.of(createService(SessionService.class)
                .logIn(Credentials.of("superadmin", "superadmin"))
                .getToken());
    }

    @AfterAll
    static void afterAll() throws IOException {
        support.after();
        MoreFiles.deleteRecursively(baseDataDir, RecursiveDeleteOption.ALLOW_INSECURE);
    }

    protected static <T> T createService(Class<T> serviceClass) {
        return JaxRsClients.create(
                serviceClass,
                "http://localhost:" + support.getLocalPort(),
                UserAgent.of(UserAgent.Agent.of("test", UserAgent.Agent.DEFAULT_VERSION)));
    }
}
