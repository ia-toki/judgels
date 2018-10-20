package judgels.sealtiel.api;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.GenericType;
import org.junit.jupiter.api.Test;

class HealthCheckIntegrationTests extends AbstractServiceIntegrationTests {
    @Test
    void rabbitmq_healthcheck() {
        Map<String, Map<String, Boolean>> result = createAdminWebTarget()
                .path("/healthcheck")
                .request(APPLICATION_JSON)
                .get()
                .readEntity(new GenericType<HashMap<String, Map<String, Boolean>>>() {});

        assertThat(result.get("rabbitmq").get("healthy")).isTrue();
    }
}
