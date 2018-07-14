package judgels.uriel.api.mocks;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableMap;
import javax.ws.rs.core.HttpHeaders;
import judgels.service.api.actor.AuthHeader;

public class MockJophiel {
    public static final String ADMIN_BEARER_TOKEN = "adminToken";
    public static final String USER_A_BEARER_TOKEN = "userAToken";
    public static final String USER_B_BEARER_TOKEN = "userBToken";

    public static final AuthHeader ADMIN_HEADER = AuthHeader.of(ADMIN_BEARER_TOKEN);
    public static final AuthHeader USER_A_HEADER = AuthHeader.of(USER_A_BEARER_TOKEN);
    public static final AuthHeader USER_B_HEADER = AuthHeader.of(USER_B_BEARER_TOKEN);

    public static final String ADMIN_JID = "adminJid";
    public static final String USER_A_JID = "userAJid";
    public static final String USER_B_JID = "userBJid";

    public static final int JOPHIEL_PORT = 9001;

    private MockJophiel() {}

    public static WireMockServer mockJophiel() {
        WireMockServer mockJophiel = new WireMockServer(JOPHIEL_PORT);

        mockJophiel.stubFor(get("/api/v2/users/me/")
                .willReturn(okForJson(ImmutableMap.of(
                        "jid", "nonadminJid",
                        "username", "nonadmin"))));

        mockJophiel.stubFor(get("/api/v2/users/me/")
                .withHeader(HttpHeaders.AUTHORIZATION, containing(ADMIN_BEARER_TOKEN))
                .willReturn(okForJson(ImmutableMap.of(
                        "jid", "adminJid",
                        "username", "admin"))));

        mockJophiel.stubFor(get("/api/v2/users/me/")
                .withHeader(HttpHeaders.AUTHORIZATION, containing(USER_A_BEARER_TOKEN))
                .willReturn(okForJson(ImmutableMap.of(
                        "jid", USER_A_JID,
                        "username", "userA"))));

        mockJophiel.stubFor(get("/api/v2/users/me/")
                .withHeader(HttpHeaders.AUTHORIZATION, containing(USER_B_BEARER_TOKEN))
                .willReturn(okForJson(ImmutableMap.of(
                        "jid", USER_B_JID,
                        "username", "userB",
                        "email", "userb@mailinator.com"))));

        return mockJophiel;
    }
}
