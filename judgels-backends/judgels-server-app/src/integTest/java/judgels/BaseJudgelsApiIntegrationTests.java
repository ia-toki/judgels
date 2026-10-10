package judgels;

import static jakarta.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import io.dropwizard.core.server.DefaultServerFactory;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.testing.DropwizardTestSupport;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import judgels.api.catalog.lesson.Lesson;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.ProblemCreateData;
import judgels.api.catalog.problem.ProblemStatement;
import judgels.api.catalog.problem.ProblemType;
import judgels.api.catalog.problem.bundle.ItemConfig;
import judgels.api.catalog.problem.bundle.ItemType;
import judgels.api.catalog.problem.bundle.item.ProblemItemCreateData;
import judgels.api.catalog.problem.bundle.item.ProblemItemUpdateData;
import judgels.api.catalog.problem.editorial.ProblemEditorialCreateData;
import judgels.api.session.Credentials;
import judgels.api.session.Session;
import judgels.api.user.User;
import judgels.api.user.UserData;
import judgels.api.user.role.UserRole;
import judgels.client.ProblemClient;
import judgels.client.ProblemEditorialClient;
import judgels.client.ProblemItemClient;
import judgels.client.ProblemStatementClient;
import judgels.client.SessionClient;
import judgels.client.UserClient;
import judgels.client.UserRoleClient;
import judgels.core.BaseJudgelsAppIntegrationTests;
import judgels.core.JudgelsAppConfiguration;
import judgels.core.feign.FeignClients;
import judgels.core.mailer.MailerConfiguration;
import judgels.core.messaging.RabbitMQConfiguration;
import judgels.grading.JudgelsServerGradingConfiguration;
import judgels.training.TrainingConfiguration;
import judgels.training.stats.StatsConfiguration;
import judgels.user.account.UserResetPasswordConfiguration;
import judgels.user.registration.UserRegistrationConfiguration;
import judgels.user.superadmin.SuperadminCreatorConfiguration;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.h2.Driver;
import org.hibernate.dialect.H2Dialect;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseJudgelsApiIntegrationTests extends BaseJudgelsAppIntegrationTests {
    private static DropwizardTestSupport<JudgelsServerApplicationConfiguration> support;
    private static Path baseDataDir;

    protected static User admin;
    protected static User user;

    protected static String superadminToken;
    protected static String adminToken;
    protected static String userToken;

    protected static WebTarget webTarget;

    @BeforeAll
    static void startApp() throws Exception {
        DataSourceFactory dbConfig = new DataSourceFactory();
        dbConfig.setDriverClass(Driver.class.getName());
        dbConfig.setUrl("jdbc:h2:mem:test");
        dbConfig.setProperties(ImmutableMap.<String, String>builder()
                .put(DIALECT, H2Dialect.class.getName())
                .put(HBM2DDL_AUTO, "create")
                .put(GENERATE_STATISTICS, "false")
                .build());

        WebSecurityConfiguration webSecurityConfig = new WebSecurityConfiguration.Builder()
                .build();

        baseDataDir = Files.createTempDirectory("judgels");

        setEditionAsTLX();

        JudgelsAppConfiguration judgelsAppConfig = new JudgelsAppConfiguration.Builder()
                .build();

        JudgelsServerGradingConfiguration gradingConfig = new JudgelsServerGradingConfiguration.Builder()
                .gradingRequestQueueName("grading-request")
                .gradingResponseQueueName("grading-response")
                .build();

        JudgelsServerConfiguration.Builder judgelsConfigBuilder = new JudgelsServerConfiguration.Builder()
                .baseDataDir(baseDataDir.toAbsolutePath())
                .appConfig(judgelsAppConfig)
                .gradingConfig(gradingConfig)
                .mailerConfig(new MailerConfiguration.Builder()
                        .host("localhost")
                        .port(9250)
                        .useSsl(false)
                        .username("wiser")
                        .password("wiser")
                        .sender("noreply@wiser.com")
                        .build())
                .userRegistrationConfig(UserRegistrationConfiguration.DEFAULT)
                .userResetPasswordConfig(UserResetPasswordConfiguration.DEFAULT)
                .superadminCreatorConfig(SuperadminCreatorConfiguration.DEFAULT);

        if (System.getenv("CI") != null) {
            judgelsConfigBuilder.rabbitMQConfig(RabbitMQConfiguration.DEFAULT);
        }

        JudgelsServerConfiguration judgelsConfig = judgelsConfigBuilder.build();

        TrainingConfiguration trainingConfig = new TrainingConfiguration.Builder()
                .statsConfig(StatsConfiguration.DEFAULT)
                .build();

        JudgelsServerApplicationConfiguration config = new JudgelsServerApplicationConfiguration(
                dbConfig,
                webSecurityConfig,
                judgelsConfig,
                Optional.of(trainingConfig)) {
            {
                DefaultServerFactory serverFactory = (DefaultServerFactory) getServerFactory();

                HttpConnectorFactory appConnector = new HttpConnectorFactory();
                appConnector.setPort(9090);
                serverFactory.setApplicationConnectors(List.of(appConnector));

                HttpConnectorFactory adminConnector = new HttpConnectorFactory();
                adminConnector.setPort(9091);
                serverFactory.setAdminConnectors(List.of(adminConnector));
            }
        };

        support = new DropwizardTestSupport<>(JudgelsServerApplication.class, config);
        support.before();

        Session superadminSession = createClient(SessionClient.class).logIn(Credentials.of("superadmin", "superadmin"));
        superadminToken = superadminSession.getToken();

        admin = createUser("admin");
        adminToken = getToken(admin);
        createClient(UserRoleClient.class).setRoles(superadminToken, Map.of("admin", new UserRole.Builder()
                .account("ADMIN")
                .problem("ADMIN")
                .contest("ADMIN")
                .training("ADMIN")
                .build()));

        user = createUser("user");
        userToken = getToken(user);
    }

    @AfterAll
    static void stopApp() throws IOException {
        support.after();
        MoreFiles.deleteRecursively(baseDataDir, RecursiveDeleteOption.ALLOW_INSECURE);
    }

    protected static WebTarget createWebTarget() {
        return JerseyClientBuilder.createClient()
                .property("jersey.config.client.followRedirects", false)
                .register(MultiPartFeature.class)
                .target("http://localhost:" + support.getLocalPort());
    }

    protected static <T> T createClient(Class<T> clientClass) {
        return FeignClients.create(clientClass, getLocalUrl());
    }

    protected static String getLocalUrl() {
        return "http://localhost:" + support.getLocalPort();
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
        return createClient(UserClient.class).createUser(superadminToken, new UserData.Builder()
                .username(username)
                .password("pass")
                .email(username + "@domain.com")
                .build());
    }

    protected static String getToken(User user) {
        return createClient(SessionClient.class)
                .logIn(Credentials.of(user.getUsername(), "pass"))
                .getToken();
    }

    protected static Problem createBundleProblem(String token, String slug) {
        return createProblem(token, slug, "Bundle");
    }

    protected static Problem createProblem(String token, String slug) {
        return createProblem(token, slug, "Batch");
    }

    protected static Problem createProblem(String token, String slug, String gradingEngine) {
        return createClient(ProblemClient.class).createProblem(token, new ProblemCreateData.Builder()
                .slug(slug)
                .gradingEngine(gradingEngine)
                .additionalNote("")
                .initialLanguage("en-US")
                .build());
    }

    // Only a problem admin can create a problem through the API.
    // This goes through michael instead, for tests whose author must not be one.
    protected static Problem createProblemViaMichael(String token, String slug, String gradingEngine) {
        Form form = new Form();
        form.param("slug", slug);
        form.param("gradingEngine", gradingEngine);
        form.param("additionalNote", "");
        form.param("initialLanguage", "en-US");

        Response response = webTarget
                .path("/problems/new")
                .request()
                .cookie(new Cookie("JUDGELS_TOKEN", token))
                .post(Entity.entity(form, APPLICATION_FORM_URLENCODED));

        String redirect = response.getLocation().toString();

        Pattern pattern = Pattern.compile("/(\\d+)/");
        Matcher matcher = pattern.matcher(redirect);
        matcher.find();

        long problemId = Long.valueOf(matcher.group(1));

        response = webTarget
                .path("/problems/" + problemId)
                .request()
                .cookie(new Cookie("JUDGELS_TOKEN", token))
                .get();

        String html = response.readEntity(String.class);

        pattern = Pattern.compile("(JID(?:BUND|PROG)[a-zA-Z0-9]+)");
        matcher = pattern.matcher(html);
        matcher.find();

        String problemJid = matcher.group(1);

        return new Problem.Builder()
                .id(problemId)
                .jid(problemJid)
                .slug(slug)
                .authorJid("JIDUSERxxx")
                .additionalNote("")
                .lastUpdateTime(Instant.now())
                .type(gradingEngine.equals("Bundle") ? ProblemType.BUNDLE : ProblemType.PROGRAMMING)
                .build();
    }

    protected static void updateProblemStatement(String token, Problem problem, String title, String text) {
        createClient(ProblemStatementClient.class).updateStatement(token, problem.getJid(), "en-US", new ProblemStatement.Builder()
                .title(title)
                .text(text)
                .build());

        // The update is committed through michael until versions have an API.
        Form form = new Form();
        form.param("title", "Update title");
        form.param("description", "");

        webTarget
                .path("/problems/" + problem.getId() + "/versions/local")
                .request()
                .cookie(new Cookie("JUDGELS_TOKEN", token))
                .post(Entity.entity(form, APPLICATION_FORM_URLENCODED));
    }

    protected static String createBundleProblemItem(
            String token,
            Problem problem,
            ItemType type,
            String meta,
            ItemConfig config) {

        ProblemItemClient itemClient = createClient(ProblemItemClient.class);

        String itemJid = itemClient
                .createItem(token, problem.getJid(), new ProblemItemCreateData.Builder().type(type).build())
                .getJid();
        itemClient.updateItem(token, problem.getJid(), itemJid, "en-US", new ProblemItemUpdateData.Builder()
                .type(type)
                .meta(meta)
                .config(config)
                .build());

        // The item is committed through michael until versions have an API.
        Form form = new Form();
        form.param("title", "Add item");
        form.param("description", "");

        webTarget
                .path("/problems/" + problem.getId() + "/versions/local")
                .request()
                .cookie(new Cookie("JUDGELS_TOKEN", token))
                .post(Entity.entity(form, APPLICATION_FORM_URLENCODED));

        return itemJid;
    }

    protected static void createProblemEditorial(String token, Problem problem) {
        createClient(ProblemEditorialClient.class).createEditorial(token, problem.getJid(), new ProblemEditorialCreateData.Builder()
                .initialLanguage("en-US")
                .build());

        // The editorial is committed through michael until versions have an API.
        Form form = new Form();
        form.param("title", "Add editorial");
        form.param("description", "");

        webTarget
                .path("/problems/" + problem.getId() + "/versions/local")
                .request()
                .cookie(new Cookie("JUDGELS_TOKEN", token))
                .post(Entity.entity(form, APPLICATION_FORM_URLENCODED));
    }

    protected static Lesson createLesson(String token, String slug) {
        Form form = new Form();
        form.param("slug", slug);
        form.param("additionalNote", "");
        form.param("initialLanguage", "en-US");

        Response response = webTarget
                .path("/lessons/new")
                .request()
                .cookie(new Cookie("JUDGELS_TOKEN", token))
                .post(Entity.entity(form, APPLICATION_FORM_URLENCODED));

        String redirect = response.getLocation().toString();

        Pattern pattern = Pattern.compile("/(\\d+)/");
        Matcher matcher = pattern.matcher(redirect);
        matcher.find();

        long lessonId = Long.valueOf(matcher.group(1));

        response = webTarget
                .path("/lessons/" + lessonId)
                .request()
                .cookie(new Cookie("JUDGELS_TOKEN", token))
                .get();

        String html = response.readEntity(String.class);

        pattern = Pattern.compile("(JIDLESS[a-zA-Z0-9]+)");
        matcher = pattern.matcher(html);
        matcher.find();

        String lessonJid = matcher.group(1);

        return new Lesson.Builder()
                .id(lessonId)
                .jid(lessonJid)
                .slug(slug)
                .authorJid("JIDUSERxxx")
                .additionalNote("")
                .lastUpdateTime(Instant.now())
                .build();
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
