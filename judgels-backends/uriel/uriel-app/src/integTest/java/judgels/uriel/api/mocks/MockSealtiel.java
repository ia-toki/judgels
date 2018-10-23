package judgels.uriel.api.mocks;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

import com.github.tomakehurst.wiremock.WireMockServer;
import javax.ws.rs.core.HttpHeaders;

public class MockSealtiel {
    public static final int SEALTIEL_PORT = 9003;

    private MockSealtiel() {}

    public static WireMockServer mockSealtiel() {
        WireMockServer mockSealtiel = new WireMockServer(SEALTIEL_PORT);

        mockSealtiel.stubFor(post("/api/v2/messages/send")
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(ok()));

        return mockSealtiel;
    }
}
