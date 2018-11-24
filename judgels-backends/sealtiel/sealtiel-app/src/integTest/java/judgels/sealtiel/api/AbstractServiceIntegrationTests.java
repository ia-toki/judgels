package judgels.sealtiel.api;

import com.palantir.conjure.java.api.config.service.UserAgent;
import io.dropwizard.testing.DropwizardTestSupport;
import javax.ws.rs.client.WebTarget;
import judgels.sealtiel.SealtielApplication;
import judgels.sealtiel.SealtielApplicationConfiguration;
import judgels.sealtiel.SealtielConfiguration;
import judgels.sealtiel.rabbitmq.RabbitMQConfiguration;
import judgels.service.api.client.Client;
import judgels.service.jaxrs.JaxRsClients;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;

public abstract class AbstractServiceIntegrationTests {
    protected static final Client CLIENT_1 = Client.of("JIDSECL-client1", "secret1");
    protected static final Client CLIENT_2 = Client.of("JIDSECL-client2", "secret2");

    private static DropwizardTestSupport<SealtielApplicationConfiguration> support;
    private static GenericContainer rabbitmq;

    @BeforeAll
    public static void beforeAll() {
        rabbitmq = new GenericContainer("rabbitmq:3.7.4-management-alpine").withExposedPorts(5672, 15672);
        rabbitmq.start();

        SealtielConfiguration sealtielConfig = new SealtielConfiguration.Builder()
                .addClients(CLIENT_1, CLIENT_2)
                .rabbitMQConfig(new RabbitMQConfiguration.Builder()
                        .host(rabbitmq.getContainerIpAddress())
                        .port(rabbitmq.getMappedPort(5672))
                        .managementPort(rabbitmq.getMappedPort(15672))
                        .username("guest")
                        .password("guest")
                        .virtualHost("/")
                        .build())
                .build();
        SealtielApplicationConfiguration config = new SealtielApplicationConfiguration(sealtielConfig);
        support = new DropwizardTestSupport<>(SealtielApplication.class, config);
        support.before();
    }

    @AfterAll
    static void afterAll() {
        support.after();
    }

    protected static WebTarget createAdminWebTarget() {
        return JerseyClientBuilder.createClient().target("http://localhost:" + support.getAdminPort());
    }

    protected static <T> T createService(Class<T> serviceClass) {
        return JaxRsClients.create(
                serviceClass,
                "http://localhost:" + support.getLocalPort(),
                UserAgent.of(UserAgent.Agent.of("test", UserAgent.Agent.DEFAULT_VERSION)));
    }
}
