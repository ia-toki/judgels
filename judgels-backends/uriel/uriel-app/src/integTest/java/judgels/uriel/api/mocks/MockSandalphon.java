package judgels.uriel.api.mocks;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.jackson.Jackson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.HttpHeaders;

public class MockSandalphon {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    public static final String PROBLEM_1_JID = "problemJid1";
    public static final String PROBLEM_2_JID = "problemJid2";
    private static final String[] PROBLEM_JIDS = {PROBLEM_1_JID, PROBLEM_2_JID};

    public static final String PROBLEM_1_SLUG = "problemSlug1";
    public static final String PROBLEM_2_SLUG = "problemSlug2";
    private static final String[] PROBLEM_SLUGS = {PROBLEM_1_SLUG, PROBLEM_2_SLUG};

    public static final int SANDALPHON_PORT = 9002;

    private MockSandalphon() {}

    public static WireMockServer mockSandalphon() {
        WireMockServer mockSandalphon = new WireMockServer(wireMockConfig()
                .port(SANDALPHON_PORT)
                .extensions(new TranslateAllowedSlugToJidsTransformer()));

        mockSandalphon.stubFor(get("/api/v2/client/problems/" + PROBLEM_1_JID + "/submission-config")
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okForJson(ImmutableMap.of(
                        "sourceKeys", ImmutableMap.of("source", "Source"),
                        "gradingEngine", "Batch",
                        "gradingLanguageRestriction", ImmutableMap.of("allowedLanguageNames", ImmutableSet.of())))));

        mockSandalphon.stubFor(post(urlPathEqualTo("/api/v2/client/problems/allowed-slug-to-jid"))
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(aResponse().withStatus(200).withTransformers("allowed-slug-to-jid")));

        mockSandalphon.stubFor(post("/api/v2/client/problems/jids")
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okForJson(ImmutableMap.of(
                        PROBLEM_1_JID, ImmutableMap.of(
                                "slug", PROBLEM_1_SLUG,
                                "defaultLanguage", "en",
                                "titlesByLanguage", ImmutableMap.of("en", "Problem 1"))))));

        return mockSandalphon;
    }

    static class TranslateAllowedSlugToJidsTransformer extends ResponseDefinitionTransformer {
        @Override
        public ResponseDefinition transform(
                Request request,
                ResponseDefinition responseDefinition,
                FileSource files,
                Parameters parameters) {

            Set<String> slugs;
            try {
                slugs = MAPPER.readValue(request.getBody(), new TypeReference<Set<String>>() {});
            } catch (IOException e) {
                return responseDefinition;
            }

            Map<String, String> res = new HashMap<>();
            for (int i = 0; i < PROBLEM_SLUGS.length; i++) {
                if (slugs.contains(PROBLEM_SLUGS[i])) {
                    res.put(PROBLEM_SLUGS[i], PROBLEM_JIDS[i]);
                }
            }

            byte[] body;
            try {
                body = MAPPER.writeValueAsBytes(res);
            } catch (IOException e) {
                return responseDefinition;
            }

            return new ResponseDefinitionBuilder()
                    .withBody(body)
                    .build();
        }

        @Override
        public String getName() {
            return "allowed-slug-to-jid";
        }

        @Override
        public boolean applyGlobally() {
            return false;
        }
    }
}
