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
import judgels.jophiel.api.user.rating.UserRating;
import judgels.service.api.actor.AuthHeader;

public class MockJophiel {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    public static final String SUPERADMIN_BEARER_TOKEN = "superAdminToken";
    public static final String ADMIN_BEARER_TOKEN = "adminToken";
    public static final String MANAGER_BEARER_TOKEN = "managerToken";
    public static final String SUPERVISOR_BEARER_TOKEN = "supervisorToken";
    public static final String SUPERVISOR_A_BEARER_TOKEN = "supervisorAToken";
    public static final String SUPERVISOR_B_BEARER_TOKEN = "supervisorBToken";
    public static final String CONTESTANT_BEARER_TOKEN = "contestantToken";
    public static final String CONTESTANT_A_BEARER_TOKEN = "contestantAToken";
    public static final String CONTESTANT_B_BEARER_TOKEN = "contestantBToken";
    public static final String USER_BEARER_TOKEN = "userToken";
    public static final String USER_A_BEARER_TOKEN = "userAToken";
    public static final String USER_B_BEARER_TOKEN = "userBToken";

    private static final String[] TOKENS = {
            SUPERADMIN_BEARER_TOKEN,
            ADMIN_BEARER_TOKEN,
            MANAGER_BEARER_TOKEN,
            SUPERVISOR_BEARER_TOKEN,
            SUPERVISOR_A_BEARER_TOKEN,
            SUPERVISOR_B_BEARER_TOKEN,
            CONTESTANT_BEARER_TOKEN,
            CONTESTANT_A_BEARER_TOKEN,
            CONTESTANT_B_BEARER_TOKEN,
            USER_BEARER_TOKEN,
            USER_A_BEARER_TOKEN,
            USER_B_BEARER_TOKEN
    };

    public static final AuthHeader SUPERADMIN_HEADER = AuthHeader.of(SUPERADMIN_BEARER_TOKEN);
    public static final AuthHeader ADMIN_HEADER = AuthHeader.of(ADMIN_BEARER_TOKEN);
    public static final AuthHeader MANAGER_HEADER = AuthHeader.of(MANAGER_BEARER_TOKEN);
    public static final AuthHeader SUPERVISOR_HEADER = AuthHeader.of(SUPERVISOR_BEARER_TOKEN);
    public static final AuthHeader SUPERVISOR_A_HEADER = AuthHeader.of(SUPERVISOR_A_BEARER_TOKEN);
    public static final AuthHeader SUPERVISOR_B_HEADER = AuthHeader.of(SUPERVISOR_B_BEARER_TOKEN);
    public static final AuthHeader CONTESTANT_HEADER = AuthHeader.of(CONTESTANT_BEARER_TOKEN);
    public static final AuthHeader CONTESTANT_A_HEADER = AuthHeader.of(CONTESTANT_A_BEARER_TOKEN);
    public static final AuthHeader CONTESTANT_B_HEADER = AuthHeader.of(CONTESTANT_B_BEARER_TOKEN);
    public static final AuthHeader USER_HEADER = AuthHeader.of(USER_BEARER_TOKEN);
    public static final AuthHeader USER_A_HEADER = AuthHeader.of(USER_A_BEARER_TOKEN);
    public static final AuthHeader USER_B_HEADER = AuthHeader.of(USER_B_BEARER_TOKEN);

    public static final String SUPERADMIN = "superadmin";
    public static final String ADMIN = "admin";
    public static final String MANAGER = "manager";
    public static final String SUPERVISOR = "supervisor";
    public static final String SUPERVISOR_A = "supervisorA";
    public static final String SUPERVISOR_B = "supervisorB";
    public static final String CONTESTANT = "contestant";
    public static final String CONTESTANT_A = "contestantA";
    public static final String CONTESTANT_B = "contestantB";
    public static final String USER = "user";
    public static final String USER_A = "userA";
    public static final String USER_B = "userB";

    private static final String[] USERNAMES = {
            SUPERADMIN,
            ADMIN,
            MANAGER,
            SUPERVISOR,
            SUPERVISOR_A,
            SUPERVISOR_B,
            CONTESTANT,
            CONTESTANT_A,
            CONTESTANT_B,
            USER,
            USER_A,
            USER_B
    };

    public static final String SUPERADMIN_JID = "superadminJid";
    public static final String ADMIN_JID = "adminJid";
    public static final String MANAGER_JID = "managerJid";
    public static final String SUPERVISOR_JID = "supervisorJid";
    public static final String SUPERVISOR_A_JID = "supervisorAJid";
    public static final String SUPERVISOR_B_JID = "supervisorBJid";
    public static final String CONTESTANT_JID = "contestantJid";
    public static final String CONTESTANT_A_JID = "contestantAJid";
    public static final String CONTESTANT_B_JID = "contestantBJid";
    public static final String USER_JID = "userJid";
    public static final String USER_A_JID = "userAJid";
    public static final String USER_B_JID = "userBJid";

    private static final String[] JIDS = {
            SUPERADMIN_JID,
            ADMIN_JID,
            MANAGER_JID,
            SUPERVISOR_JID,
            SUPERVISOR_A_JID,
            SUPERVISOR_B_JID,
            CONTESTANT_JID,
            CONTESTANT_A_JID,
            CONTESTANT_B_JID,
            USER_JID,
            USER_A_JID,
            USER_B_JID
    };

    private static final Map<String, UserRating> RATINGS = ImmutableMap.of(
            USER_A_JID, UserRating.of(2000, 2000),
            USER_B_JID, UserRating.of(1000, 1000));

    public static final int JOPHIEL_PORT = 9001;

    private MockJophiel() {}

    public static WireMockServer mockJophiel() {
        WireMockServer mockJophiel = new WireMockServer(wireMockConfig()
                .port(JOPHIEL_PORT)
                .extensions(new TranslateUsernamesToJidsTransformer(), new GetProfilesTransformer()));

        mockJophiel.stubFor(get("/api/v2/users/me/")
                .willReturn(okForJson(ImmutableMap.of(
                        "jid", "nonadminJid",
                        "username", "nonadmin",
                        "email", "nonadmin@jophiel.judgels"))));

        for (int i = 0; i < TOKENS.length; i++) {
            mockJophiel.stubFor(get("/api/v2/users/me/")
                    .withHeader(HttpHeaders.AUTHORIZATION, containing(TOKENS[i]))
                    .willReturn(okForJson(ImmutableMap.of(
                            "jid", JIDS[i],
                            "username", USERNAMES[i],
                            "email", USERNAMES[i] + "@jophiel.judgels"))));
        }

        mockJophiel.stubFor(get("/api/v2/users/me/role")
                .withHeader(HttpHeaders.AUTHORIZATION, containing(SUPERADMIN_BEARER_TOKEN))
                .willReturn(okForJson(ImmutableMap.of("jophiel", "SUPERADMIN"))));

        mockJophiel.stubFor(get("/api/v2/users/me/role")
                .withHeader(HttpHeaders.AUTHORIZATION, containing(ADMIN_BEARER_TOKEN))
                .willReturn(okForJson(ImmutableMap.of("jophiel", "ADMIN", "uriel", "ADMIN"))));

        mockJophiel.stubFor(get("/api/v2/users/me/role")
                .withHeader(HttpHeaders.AUTHORIZATION, containing(USER_BEARER_TOKEN))
                .willReturn(okForJson(ImmutableMap.of("jophiel", "USER"))));

        mockJophiel.stubFor(get("/api/v2/client/users/" + SUPERADMIN_JID + "/role")
                .willReturn(okForJson(ImmutableMap.of("jophiel", "SUPERADMIN"))));

        mockJophiel.stubFor(get("/api/v2/client/users/" + ADMIN_JID + "/role")
                .willReturn(okForJson(ImmutableMap.of("jophiel", "ADMIN", "uriel", "ADMIN"))));

        mockJophiel.stubFor(get("/api/v2/client/users/" + MANAGER_JID + "/role")
                .willReturn(okForJson(ImmutableMap.of("jophiel", "USER"))));

        mockJophiel.stubFor(get("/api/v2/client/users/" + SUPERVISOR_JID + "/role")
                .willReturn(okForJson(ImmutableMap.of("jophiel", "USER"))));

        mockJophiel.stubFor(get("/api/v2/client/users/" + SUPERVISOR_A_JID + "/role")
                .willReturn(okForJson(ImmutableMap.of("jophiel", "USER"))));

        mockJophiel.stubFor(get("/api/v2/client/users/" + SUPERVISOR_B_JID + "/role")
                .willReturn(okForJson(ImmutableMap.of("jophiel", "USER"))));

        mockJophiel.stubFor(get("/api/v2/client/users/" + CONTESTANT_JID + "/role")
                .willReturn(okForJson(ImmutableMap.of("jophiel", "USER"))));

        mockJophiel.stubFor(get("/api/v2/client/users/" + CONTESTANT_A_JID + "/role")
                .willReturn(okForJson(ImmutableMap.of("jophiel", "USER"))));

        mockJophiel.stubFor(get("/api/v2/client/users/" + CONTESTANT_B_JID + "/role")
                .willReturn(okForJson(ImmutableMap.of("jophiel", "USER"))));

        mockJophiel.stubFor(get("/api/v2/client/users/" + USER_JID + "/role")
                .willReturn(okForJson(ImmutableMap.of("jophiel", "USER"))));

        mockJophiel.stubFor(get("/api/v2/client/users/" + USER_A_JID + "/role")
                .willReturn(okForJson(ImmutableMap.of("jophiel", "USER"))));

        mockJophiel.stubFor(get("/api/v2/client/users/" + USER_B_JID + "/role")
                .willReturn(okForJson(ImmutableMap.of("jophiel", "USER"))));

        mockJophiel.stubFor(get("/api/v2/client/users/nonadminJid/role")
                .willReturn(okForJson(ImmutableMap.of("jophiel", "USER"))));

        mockJophiel.stubFor(get("/api/v2/client/users/guest/role")
                .willReturn(okForJson(ImmutableMap.of("jophiel", "GUEST"))));

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
            for (int i = 0; i < TOKENS.length; i++) {
                if (usernames.contains(USERNAMES[i])) {
                    res.put(USERNAMES[i], JIDS[i]);
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
            for (int i = 0; i < TOKENS.length; i++) {
                if (userJids.contains(JIDS[i])) {
                    Profile.Builder profile = new Profile.Builder().username(USERNAMES[i]);
                    if (RATINGS.containsKey(JIDS[i])) {
                        profile.rating(RATINGS.get(JIDS[i]));
                    }
                    res.put(JIDS[i], profile.build());
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
            return "get-profiles";
        }

        @Override
        public boolean applyGlobally() {
            return false;
        }
    }
}
