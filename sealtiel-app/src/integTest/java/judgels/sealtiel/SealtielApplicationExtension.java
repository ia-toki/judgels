package judgels.sealtiel;

import com.palantir.remoting.api.config.service.ServiceConfiguration;
import com.palantir.remoting.api.config.ssl.SslConfiguration;
import com.palantir.remoting3.clients.ClientConfiguration;
import com.palantir.remoting3.clients.ClientConfigurations;
import com.palantir.remoting3.clients.UserAgent;
import com.palantir.remoting3.jaxrs.JaxRsClient;
import io.dropwizard.testing.DropwizardTestSupport;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.ws.rs.client.WebTarget;
import judgels.sealtiel.rabbitmq.RabbitMQConfiguration;
import judgels.service.api.client.Client;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;

public class SealtielApplicationExtension implements BeforeAllCallback, AfterAllCallback {
    public static final Client CLIENT_1 = Client.of("JIDSECL-client1", "secret1");
    public static final Client CLIENT_2 = Client.of("JIDSECL-client2", "secret2");

    private static DropwizardTestSupport<SealtielApplicationConfiguration> sealtiel;
    private static GenericContainer rabbitmq;
    private static GenericContainer rabbitmqMgmt;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        rabbitmq = new GenericContainer("rabbitmq:3.7.2").withExposedPorts(5672);
        rabbitmq.start();
        rabbitmqMgmt = new GenericContainer("rabbitmq:3.7.2-management").withExposedPorts(15672);
        rabbitmqMgmt.start();

        SealtielConfiguration sealtielConfig = new SealtielConfiguration.Builder()
                .addClients(CLIENT_1, CLIENT_2)
                .rabbitMQConfig(new RabbitMQConfiguration.Builder()
                        .host(rabbitmq.getContainerIpAddress())
                        .port(rabbitmq.getMappedPort(5672))
                        .managementPort(rabbitmqMgmt.getMappedPort(15672))
                        .username("guest")
                        .password("guest")
                        .virtualHost("/")
                        .build())
                .build();
        SealtielApplicationConfiguration config = new SealtielApplicationConfiguration(sealtielConfig);
        sealtiel = new DropwizardTestSupport<>(SealtielApplication.class, config);
        sealtiel.before();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        rabbitmq.stop();
        rabbitmqMgmt.stop();
        sealtiel.after();
    }

    public static WebTarget createAdminWebTarget() {
        return JerseyClientBuilder.createClient().target("http://localhost:" + sealtiel.getAdminPort());
    }

    public static <T> T createService(Class<T> serviceClass) {
        Path testTrustStore = Paths.get(
                SealtielApplicationExtension.class.getClassLoader().getResource("test.jks").getPath());
        ServiceConfiguration serviceConfig = ServiceConfiguration.builder()
                .addUris("http://localhost:" + sealtiel.getLocalPort())
                .security(SslConfiguration.of(testTrustStore))
                .build();

        UserAgent userAgent = UserAgent.of(UserAgent.Agent.of("test", UserAgent.Agent.DEFAULT_VERSION));
        ClientConfiguration clientConfig = ClientConfigurations.of(serviceConfig);
        return JaxRsClient.create(serviceClass, userAgent, clientConfig);
    }
}
