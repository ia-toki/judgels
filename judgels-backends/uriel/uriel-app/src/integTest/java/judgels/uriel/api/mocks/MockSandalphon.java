package judgels.uriel.api.mocks;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import javax.ws.rs.core.HttpHeaders;

public class MockSandalphon {
    public static final String PROBLEM_1_JID = "problemJid1";

    public static final int SANDALPHON_PORT = 9002;

    private MockSandalphon() {}

    public static WireMockServer mockSandalphon() {
        WireMockServer mockSandalphon = new WireMockServer(SANDALPHON_PORT);

        mockSandalphon.stubFor(get("/api/v2/client/problems/" + PROBLEM_1_JID + "/submission-config")
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okForJson(ImmutableMap.of(
                        "sourceKeys", ImmutableMap.of("source", "Source"),
                        "gradingEngine", "Batch",
                        "gradingLanguageRestriction", ImmutableMap.of("allowedLanguageNames", ImmutableSet.of())))));

        return mockSandalphon;
    }
}
