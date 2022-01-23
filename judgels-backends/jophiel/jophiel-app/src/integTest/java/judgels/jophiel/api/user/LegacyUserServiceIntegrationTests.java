package judgels.jophiel.api.user;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import org.junit.jupiter.api.Test;

public class LegacyUserServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private final WebTarget webTarget = createWebTarget();

    @Test
    void autocomplete_users() {
        createUser("andi");
        createUser("dimas");
        createUser("ani");

        Response response = webTarget
                .path("/api/v2/users/autocomplete")
                .queryParam("term", "di")
                .queryParam("callback", "fn")
                .request()
                .get();

        String responseBody = response.readEntity(String.class);
        assertThat(responseBody).matches("fn\\(\\[\\{.*dimas.*},\\{.*andi.*}]\\);");
    }
}
