package judgels.jerahmeel.api.mocks;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class MockUriel {
    public static final String CONTEST_1_JID = "contestJid1";
    public static final String CONTEST_2_JID = "contestJid2";

    public static final String CONTEST_1_SLUG = "contestSlug1";
    public static final String CONTEST_2_SLUG = "contestSlug2";

    public static final int URIEL_PORT = 9004;

    private MockUriel() {}

    // CHECKSTYLE.OFF: MethodLengthCheck
    public static WireMockServer mockUriel() {
    // CHECKSTYLE.ON: MethodLengthCheck

        WireMockServer mockUriel = new WireMockServer(wireMockConfig()
                .port(URIEL_PORT));

        Map<String, Object> contest1 = new ImmutableMap.Builder<String, Object>()
                .put("id", 1)
                .put("jid", CONTEST_1_JID)
                .put("slug", CONTEST_1_SLUG)
                .put("name", "Contest 1 Name")
                .put("style", "ICPC")
                .put("beginTime", 123)
                .put("duration", 456)
                .build();

        Map<String, Object> contest2 = new ImmutableMap.Builder<String, Object>()
                .put("id", 2)
                .put("jid", CONTEST_2_JID)
                .put("slug", CONTEST_2_SLUG)
                .put("name", "Contest 2 Name")
                .put("style", "ICPC")
                .put("beginTime", 123)
                .put("duration", 456)
                .build();

        mockUriel.stubFor(get("/api/v2/contests/" + CONTEST_1_JID)
                .willReturn(okForJson(contest1)));

        mockUriel.stubFor(get("/api/v2/contests/" + CONTEST_2_JID)
                .willReturn(okForJson(contest2)));

        mockUriel.stubFor(get("/api/v2/contests/slug/" + CONTEST_1_SLUG)
                .willReturn(okForJson(contest1)));

        mockUriel.stubFor(get("/api/v2/contests/slug/" + CONTEST_2_SLUG)
                .willReturn(okForJson(contest2)));

        mockUriel.stubFor(get("/api/v2/contests/slug/bogus")
                .willReturn(notFound()
                        .withBody("{\"code\": 404, \"message\": \"NOT_FOUND\"}")
                        .withHeader("Content-Type", "application/json")));

        return mockUriel;
    }
}
