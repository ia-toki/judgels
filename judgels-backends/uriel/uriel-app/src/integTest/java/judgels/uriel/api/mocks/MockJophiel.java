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
import io.dropwizard.jackson.Jackson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.HttpHeaders;
import judgels.jophiel.api.profile.Profile;
import judgels.service.api.actor.AuthHeader;

public class MockJophiel {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    public static final String SUPERADMIN_BEARER_TOKEN = "superadminToken";
    public static final String ADMIN_BEARER_TOKEN = "adminToken";
    public static final String USER_A_BEARER_TOKEN = "userAToken";
    public static final String USER_B_BEARER_TOKEN = "userBToken";

    public static final AuthHeader SUPERADMIN_HEADER = AuthHeader.of(SUPERADMIN_BEARER_TOKEN);
    public static final AuthHeader ADMIN_HEADER = AuthHeader.of(ADMIN_BEARER_TOKEN);
    public static final AuthHeader USER_A_HEADER = AuthHeader.of(USER_A_BEARER_TOKEN);
    public static final AuthHeader USER_B_HEADER = AuthHeader.of(USER_B_BEARER_TOKEN);

    public static final String SUPERADMIN = "superadmin";
    public static final String ADMIN = "admin";
    public static final String USER_A = "userA";
    public static final String USER_B = "userB";

    public static final String SUPERADMIN_JID = "superadminJid";
    public static final String ADMIN_JID = "adminJid";
    public static final String USER_A_JID = "userAJid";
    public static final String USER_B_JID = "userBJid";

    public static final int JOPHIEL_PORT = 9001;

    private MockJophiel() {}

    public static WireMockServer mockJophiel() {
        WireMockServer mockJophiel = new WireMockServer(wireMockConfig()
                .port(JOPHIEL_PORT)
                .extensions(new TranslateUsernamesToJidsTransformer(), new GetProfilesTransformer()));

        mockJophiel.stubFor(get("/api/v2/users/me/")
                .willReturn(okForJson(ImmutableMap.of(
                        "jid", "nonadminJid",
                        "username", "nonadmin"))));

        mockJophiel.stubFor(get("/api/v2/users/me/")
                .withHeader(HttpHeaders.AUTHORIZATION, containing(ADMIN_BEARER_TOKEN))
                .willReturn(okForJson(ImmutableMap.of(
                        "jid", ADMIN_JID,
                        "username", ADMIN))));

        mockJophiel.stubFor(get("/api/v2/users/me/")
                .withHeader(HttpHeaders.AUTHORIZATION, containing(USER_A_BEARER_TOKEN))
                .willReturn(okForJson(ImmutableMap.of(
                        "jid", USER_A_JID,
                        "username", USER_A))));

        mockJophiel.stubFor(get("/api/v2/users/me/")
                .withHeader(HttpHeaders.AUTHORIZATION, containing(USER_B_BEARER_TOKEN))
                .willReturn(okForJson(ImmutableMap.of(
                        "jid", USER_B_JID,
                        "username", USER_B,
                        "email", "userb@mailinator.com"))));

        mockJophiel.stubFor(get("/api/v2/users/me/role")
                .withHeader(HttpHeaders.AUTHORIZATION, containing(SUPERADMIN_BEARER_TOKEN))
                .willReturn(okForJson("superadmin")));

        mockJophiel.stubFor(post("/api/v2/user-search/username-to-jid")
                .willReturn(aResponse().withStatus(200).withTransformers("username-to-jid")));

        mockJophiel.stubFor(post(urlPathEqualTo("/api/v2/profiles/"))
                .willReturn(aResponse().withStatus(200).withTransformers("get-profiles")));

        return mockJophiel;
    }

    static class TranslateUsernamesToJidsTransformer extends ResponseDefinitionTransformer {
        @Override
        public ResponseDefinition transform(
                Request request,
                ResponseDefinition responseDefinition,
                FileSource files,
                Parameters parameters) {

            Set<String> usernames;
            try {
                usernames = MAPPER.readValue(request.getBody(), new TypeReference<Set<String>>() {});
            } catch (IOException e) {
                return responseDefinition;
            }

            Map<String, String> res = new HashMap<>();
            if (usernames.contains(SUPERADMIN)) {
                res.put(SUPERADMIN, SUPERADMIN_JID);
            }
            if (usernames.contains(ADMIN)) {
                res.put(ADMIN, ADMIN_JID);
            }
            if (usernames.contains(USER_A)) {
                res.put(USER_A, USER_A_JID);
            }
            if (usernames.contains(USER_B)) {
                res.put(USER_B, USER_B_JID);
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
            return "username-to-jid";
        }

        @Override
        public boolean applyGlobally() {
            return false;
        }
    }

    static class GetProfilesTransformer extends ResponseDefinitionTransformer {
        @Override
        public ResponseDefinition transform(
                Request request,
                ResponseDefinition responseDefinition,
                FileSource files,
                Parameters parameters) {

            Set<String> userJids;
            try {
                userJids = MAPPER.readValue(request.getBody(), new TypeReference<Set<String>>() {});
            } catch (IOException e) {
                return responseDefinition;
            }

            Map<String, Profile> res = new HashMap<>();

            if (userJids.contains(SUPERADMIN_JID)) {
                res.put(SUPERADMIN_JID, new Profile.Builder().username(SUPERADMIN).build());
            }
            if (userJids.contains(ADMIN_JID)) {
                res.put(ADMIN_JID, new Profile.Builder().username(ADMIN).build());
            }
            if (userJids.contains(USER_A_JID)) {
                res.put(USER_A_JID, new Profile.Builder().username(USER_A).build());
            }
            if (userJids.contains(USER_B_JID)) {
                res.put(USER_B_JID, new Profile.Builder().username(USER_B).build());
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
            return "get-profiles";
        }

        @Override
        public boolean applyGlobally() {
            return false;
        }
    }
}
