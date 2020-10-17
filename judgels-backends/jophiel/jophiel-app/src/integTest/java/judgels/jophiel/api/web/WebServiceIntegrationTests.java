package judgels.jophiel.api.web;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.api.AbstractServiceIntegrationTests;
import org.junit.jupiter.api.Test;

class WebServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private WebService webService = createService(WebService.class);

    @Test
    void getWebConfig() {
        WebConfig config = webService.getWebConfig();

        assertThat(config.getAnnouncements()).isEmpty();
    }
}
