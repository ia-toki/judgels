package judgels;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import judgels.jerahmeel.JerahmeelConfiguration;
import judgels.jerahmeel.stats.StatsConfiguration;
import judgels.jophiel.JophielConfiguration;
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.session.SessionService;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserService;
import judgels.jophiel.mailer.MailerConfiguration;
import judgels.jophiel.session.SessionConfiguration;
import judgels.jophiel.user.account.UserRegistrationConfiguration;
import judgels.jophiel.user.account.UserResetPasswordConfiguration;
import judgels.jophiel.user.avatar.UserAvatarConfiguration;
import judgels.jophiel.user.superadmin.SuperadminCreatorConfiguration;
import judgels.jophiel.user.web.WebConfiguration;
import judgels.sandalphon.SandalphonConfiguration;
import judgels.sandalphon.api.SandalphonClientConfiguration;
import judgels.service.api.actor.AuthHeader;
import judgels.service.jaxrs.JaxRsClients;
import judgels.uriel.UrielConfiguration;
import judgels.uriel.api.UrielClientConfiguration;
import judgels.uriel.file.FileConfiguration;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.h2.Driver;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseJudgelsServiceIntegrationTests {
    private static DropwizardTestSupport<JudgelsServerApplicationConfiguration> support;
    private static Path baseDataDir;

    protected static User admin;
    protected static User user;

    protected static AuthHeader adminHeader;
    protected static AuthHeader userHeader;

    @BeforeAll
    static void startApp() throws Exception {
        DataSourceFactory dbConfig = new DataSourceFactory();
        dbConfig.setDriverClass(Driver.class.getName());
        dbConfig.setUrl("jdbc:h2:mem:./judgels");
        dbConfig.setProperties(ImmutableMap.<String, String>builder()
                .put(DIALECT, H2Dialect.class.getName())
                .put(HBM2DDL_AUTO, "create")
                .put(GENERATE_STATISTICS, "false")
                .build());

        baseDataDir = Files.createTempDirectory("judgels");

        JudgelsAppConfiguration judgelsAppConfig = new JudgelsAppConfiguration.Builder()
                .name("Judgels")
                .build();

        JudgelsServerConfiguration judgelsConfig = new JudgelsServerConfiguration.Builder()
                .baseDataDir(baseDataDir.toString())
                .appConfig(judgelsAppConfig)
                .build();

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

        UrielConfiguration urielConfig = new UrielConfiguration.Builder()
                .baseDataDir(baseDataDir.toString())
                .sandalphonConfig(SandalphonClientConfiguration.DEFAULT)
                .gabrielConfig(GabrielClientConfiguration.DEFAULT)
                .submissionConfig(judgels.uriel.submission.programming.SubmissionConfiguration.DEFAULT)
                .fileConfig(FileConfiguration.DEFAULT)
                .build();

        SandalphonConfiguration sandalphonConfig = new SandalphonConfiguration.Builder()
                .baseDataDir(baseDataDir.toString())
                .jophielConfig(JophielClientConfiguration.DEFAULT)
                .gabrielConfig(GabrielClientConfiguration.DEFAULT)
                .build();

        JerahmeelConfiguration jerahmeelConfig = new JerahmeelConfiguration.Builder()
                .baseDataDir(baseDataDir.toString())
                .sandalphonConfig(SandalphonClientConfiguration.DEFAULT)
                .urielConfig(UrielClientConfiguration.DEFAULT)
                .gabrielConfig(GabrielClientConfiguration.DEFAULT)
                .submissionConfig(judgels.jerahmeel.submission.programming.SubmissionConfiguration.DEFAULT)
                .statsConfig(StatsConfiguration.DEFAULT)
                .build();

        JudgelsServerApplicationConfiguration config = new JudgelsServerApplicationConfiguration(
                dbConfig,
                WebSecurityConfiguration.DEFAULT,
                judgelsConfig,
                jophielConfig,
                sandalphonConfig,
                urielConfig,
                jerahmeelConfig);

        support = new DropwizardTestSupport<>(JudgelsServerApplication.class, config);
        support.before();

        Session adminSession = createService(SessionService.class).logIn(Credentials.of("superadmin", "superadmin"));
        adminHeader = AuthHeader.of(adminSession.getToken());
        admin = createService(UserService.class).getUser(adminHeader, adminSession.getUserJid());

        user = createUser("user");
        userHeader = getHeader(user);
    }

    @AfterAll
    static void stopApp() throws IOException {
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

    protected static void assertPermitted(ThrowingCallable callable) {
        assertThatCode(callable).doesNotThrowAnyException();
    }

    protected static AbstractThrowableAssert<?, ? extends Throwable> assertBadRequest(ThrowingCallable callable) {
        return assertThatThrownBy(callable).hasFieldOrPropertyWithValue("code", 400);
    }

    protected static void assertUnauthorized(ThrowingCallable callable) {
        assertThatThrownBy(callable).hasFieldOrPropertyWithValue("code", 401);
    }

    protected static AbstractThrowableAssert<?, ? extends Throwable> assertForbidden(ThrowingCallable callable) {
        return assertThatThrownBy(callable).hasFieldOrPropertyWithValue("code", 403);
    }

    protected static void assertNotFound(ThrowingCallable callable) {
        assertThatThrownBy(callable).hasFieldOrPropertyWithValue("code", 404);
    }

    protected static User createUser(String username) {
        return createService(UserService.class).createUser(adminHeader, new UserData.Builder()
                .username(username)
                .password("pass")
                .email(username + "@domain.com")
                .build());
    }

    protected static AuthHeader getHeader(User user) {
        return AuthHeader.of(createService(SessionService.class)
                .logIn(Credentials.of(user.getUsername(), "pass"))
                .getToken());
    }

    protected static String randomString() {
        return "string" + (Math.random() * 1000000000);
    }

    protected static ThrowingCallable callAll(ThrowingCallable... callables) {
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
}
