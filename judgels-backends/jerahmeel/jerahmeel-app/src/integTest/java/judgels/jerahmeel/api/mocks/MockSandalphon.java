package judgels.jerahmeel.api.mocks;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
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
    public static final String PROBLEM_3_JID = "problemJid3";
    private static final String[] PROBLEM_JIDS = {PROBLEM_1_JID, PROBLEM_2_JID, PROBLEM_3_JID};

    public static final String PROBLEM_1_SLUG = "problemSlug1";
    public static final String PROBLEM_2_SLUG = "problemSlug2";
    public static final String PROBLEM_3_SLUG = "problemSlug3";
    private static final String[] PROBLEM_SLUGS = {PROBLEM_1_SLUG, PROBLEM_2_SLUG, PROBLEM_3_SLUG};

    public static final String PROBLEM_1_TYPE = "PROGRAMMING";
    public static final String PROBLEM_2_TYPE = "PROGRAMMING";
    public static final String PROBLEM_3_TYPE = "BUNDLE";

    public static final String LESSON_1_JID = "lessonJid1";
    public static final String LESSON_2_JID = "lessonJid2";
    public static final String LESSON_3_JID = "lessonJid3";
    private static final String[] LESSON_JIDS = {LESSON_1_JID, LESSON_2_JID, LESSON_3_JID};

    public static final String LESSON_1_SLUG = "lessonSlug1";
    public static final String LESSON_2_SLUG = "lessonSlug2";
    public static final String LESSON_3_SLUG = "lessonSlug3";
    private static final String[] LESSON_SLUGS = {LESSON_1_SLUG, LESSON_2_SLUG, LESSON_3_SLUG};

    public static final int SANDALPHON_PORT = 9002;

    private MockSandalphon() {}

    // CHECKSTYLE.OFF: MethodLengthCheck
    public static WireMockServer mockSandalphon() {
    // CHECKSTYLE.ON: MethodLengthCheck

        WireMockServer mockSandalphon = new WireMockServer(wireMockConfig()
                .port(SANDALPHON_PORT)
                .extensions(
                        new TranslateAllowedProblemSlugToJidsTransformer(),
                        new TranslateAllowedLessonSlugToJidsTransformer()));

        mockSandalphon.stubFor(post(urlPathEqualTo("/api/v2/client/problems/allowed-slug-to-jid"))
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(aResponse().withStatus(200).withTransformers("allowed-problem-slug-to-jid")));

        mockSandalphon.stubFor(post(urlPathEqualTo("/api/v2/client/lessons/allowed-slug-to-jid"))
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(aResponse().withStatus(200).withTransformers("allowed-lesson-slug-to-jid")));

        mockSandalphon.stubFor(post("/api/v2/client/problems/jids")
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okForJson(ImmutableMap.of(
                        PROBLEM_1_JID, ImmutableMap.of(
                                "type", PROBLEM_1_TYPE,
                                "slug", PROBLEM_1_SLUG,
                                "defaultLanguage", "en",
                                "titlesByLanguage", ImmutableMap.of("en", "Problem 1")),
                        PROBLEM_2_JID, ImmutableMap.of(
                                "type", PROBLEM_2_TYPE,
                                "slug", PROBLEM_2_SLUG,
                                "defaultLanguage", "en",
                                "titlesByLanguage", ImmutableMap.of("en", "Problem 2")),
                        PROBLEM_3_JID, ImmutableMap.of(
                                "type", PROBLEM_3_TYPE,
                                "slug", PROBLEM_3_SLUG,
                                "defaultLanguage", "en",
                                "titlesByLanguage", ImmutableMap.of("en", "Problem 3"),
                                "hasEditorial", false)))));

        mockSandalphon.stubFor(post("/api/v2/client/problems/metadata/jids")
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okForJson(ImmutableMap.of())));

        mockSandalphon.stubFor(post("/api/v2/client/problems/tags/visibility")
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(ok()));

        mockSandalphon.stubFor(post("/api/v2/client/lessons/jids")
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okForJson(ImmutableMap.of(
                        PROBLEM_1_JID, ImmutableMap.of(
                                "slug", LESSON_1_SLUG,
                                "defaultLanguage", "en",
                                "titlesByLanguage", ImmutableMap.of("en", "Lesson 1")),
                        PROBLEM_2_JID, ImmutableMap.of(
                                "slug", LESSON_2_SLUG,
                                "defaultLanguage", "en",
                                "titlesByLanguage", ImmutableMap.of("en", "Lesson 2")),
                        PROBLEM_3_JID, ImmutableMap.of(
                                "slug", LESSON_3_SLUG,
                                "defaultLanguage", "en",
                                "titlesByLanguage", ImmutableMap.of("en", "Lesson 3"))))));

        /* Mocks for problem info */

        mockSandalphon.stubFor(get("/api/v2/client/problems/" + PROBLEM_1_JID)
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okForJson(ImmutableMap.of(
                        "type", PROBLEM_1_TYPE,
                        "slug", PROBLEM_1_SLUG,
                        "defaultLanguage", "en",
                        "titlesByLanguage", ImmutableMap.of("en", "Problem 1")))));

        mockSandalphon.stubFor(get("/api/v2/client/problems/" + PROBLEM_2_JID)
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okForJson(ImmutableMap.of(
                        "type", PROBLEM_2_TYPE,
                        "slug", PROBLEM_2_SLUG,
                        "defaultLanguage", "en",
                        "titlesByLanguage", ImmutableMap.of("en", "Problem 2")))));

        mockSandalphon.stubFor(get("/api/v2/client/problems/" + PROBLEM_3_JID)
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okForJson(ImmutableMap.of(
                        "type", PROBLEM_3_TYPE,
                        "slug", PROBLEM_3_SLUG,
                        "defaultLanguage", "en",
                        "titlesByLanguage", ImmutableMap.of("en", "Problem 3")))));

        /* Mocks for lesson info */

        mockSandalphon.stubFor(get("/api/v2/client/lessons/" + LESSON_1_JID)
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okForJson(ImmutableMap.of(
                        "slug", LESSON_1_SLUG,
                        "defaultLanguage", "en",
                        "titlesByLanguage", ImmutableMap.of("en", "Lesson 1")))));

        mockSandalphon.stubFor(get("/api/v2/client/lessons/" + LESSON_2_JID)
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okForJson(ImmutableMap.of(
                        "slug", LESSON_2_SLUG,
                        "defaultLanguage", "en",
                        "titlesByLanguage", ImmutableMap.of("en", "Lesson 2")))));

        mockSandalphon.stubFor(get("/api/v2/client/lessons/" + LESSON_3_JID)
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okForJson(ImmutableMap.of(
                        "slug", LESSON_3_SLUG,
                        "defaultLanguage", "en",
                        "titlesByLanguage", ImmutableMap.of("en", "Lesson 3")))));

        /* Mocks for programming problem submission config */

        mockSandalphon.stubFor(get("/api/v2/client/problems/" + PROBLEM_1_JID + "/programming/submission-config")
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okForJson(ImmutableMap.of(
                        "sourceKeys", ImmutableMap.of("source", "Source"),
                        "gradingEngine", "Batch",
                        "gradingLanguageRestriction", ImmutableMap.of("allowedLanguageNames", ImmutableSet.of())))));

        /* Mocks for problem worksheets */

        mockSandalphon.stubFor(get("/api/v2/client/problems/" + PROBLEM_1_JID + "/programming/worksheet")
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okForJson(ImmutableMap.of(
                        "statement", ImmutableMap.of(
                                "title", "Problem 1",
                                "text", "Statement for problem 1. <a href=\"render/document\">link</a>"),
                        "limits", ImmutableMap.of(
                                "timeLimit", 2000,
                                "memoryLimit", 65536),
                        "submissionConfig", ImmutableMap.of(
                                "sourceKeys", ImmutableMap.of("source", "Source"),
                                "gradingEngine", "Batch",
                                "gradingLanguageRestriction", ImmutableMap.of(
                                        "allowedLanguageNames", ImmutableSet.of()))))));

        mockSandalphon.stubFor(get("/api/v2/client/problems/" + PROBLEM_2_JID + "/programming/worksheet")
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okForJson(ImmutableMap.of(
                        "statement", ImmutableMap.of(
                                "title", "Problem 2",
                                "text", "Statement for problem 2. <img src=\"render/image\"/>"),
                        "limits", ImmutableMap.of(
                                "timeLimit", 2000,
                                "memoryLimit", 65536),
                        "submissionConfig", ImmutableMap.of(
                                "sourceKeys", ImmutableMap.of("source", "Source"),
                                "gradingEngine", "Batch",
                                "gradingLanguageRestriction", ImmutableMap.of(
                                        "allowedLanguageNames", ImmutableSet.of()))))));

        mockSandalphon.stubFor(get("/api/v2/client/problems/" + PROBLEM_3_JID + "/bundle/worksheet")
                .withHeader(HttpHeaders.AUTHORIZATION, containing("Basic"))
                .willReturn(okJson("{"
                        + "    \"statement\": {"
                        + "        \"title\": \"Problem 3\","
                        + "        \"text\": \"<h3>Statement 3</h3> <img src=\\\"render/image\\\"/>\\r\\n\""
                        + "    },"
                        + "    \"items\": ["
                        + "        {"
                        + "            \"jid\": \"JIDITEMwcAjhP4KZurUE2F5LdSb\","
                        + "            \"type\": \"STATEMENT\","
                        + "            \"meta\": \"1-2\","
                        + "            \"config\": {"
                        + "                \"statement\": \"<p>ini statement 1</p><img src=\\\"render/i\\\"/>\""
                        + "            }"
                        + "        },"
                        + "        {"
                        + "            \"jid\": \"JIDITEMPeKuqUA0Q7zvJjTQXXVD\","
                        + "            \"type\": \"MULTIPLE_CHOICE\","
                        + "            \"number\": 1,"
                        + "            \"meta\": \"1\","
                        + "            \"config\": {"
                        + "                \"score\": 1,"
                        + "                \"penalty\": 0,"
                        + "                \"choices\": ["
                        + "                    {"
                        + "                         \"alias\": \"a\","
                        + "                         \"content\": \"jawaban a <img src=\\\"render/choiceimage\\\"/>\","
                        + "                         \"isCorrect\": false"
                        + "                    },"
                        + "                    { \"alias\": \"b\", \"content\": \"jawaban b\", \"isCorrect\": true }"
                        + "                ],"
                        + "                \"statement\": \"<p>ini soal 1</p>\\r\\n\""
                        + "            }"
                        + "        },"
                        + "        {"
                        + "            \"jid\": \"JIDITEMtOoiXuIgPcD1oUsMzvbP\","
                        + "            \"type\": \"MULTIPLE_CHOICE\","
                        + "            \"number\": 2,"
                        + "            \"meta\": \"2\","
                        + "            \"config\": {"
                        + "                \"score\": 4.0,"
                        + "                \"penalty\": -1,"
                        + "                \"choices\": ["
                        + "                    { \"alias\": \"a\", \"content\": \"pilihan a\", \"isCorrect\": true },"
                        + "                    { \"alias\": \"b\", \"content\": \"pilihan b\", \"isCorrect\": false }"
                        + "                ],"
                        + "                \"statement\": \"<p>ini soal kedua</p>\\r\\n\""
                        + "            }"
                        + "        },"
                        + "        {"
                        + "            \"jid\": \"JIDITEMcD1oSDFJLadFSsMddfsf\","
                        + "            \"type\": \"SHORT_ANSWER\","
                        + "            \"number\": 3,"
                        + "            \"meta\": \"3\","
                        + "            \"config\": {"
                        + "                \"score\": 4,"
                        + "                \"penalty\": -1.0,"
                        + "                \"inputValidationRegex\": \"\\\\d+\","
                        + "                \"gradingRegex\": \"123\","
                        + "                \"statement\": \"<p>ini soal short answer</p>\\r\\n\""
                        + "            }"
                        + "        },"
                        + "        {"
                        + "            \"jid\": \"JIDITEMkhUulUkbUkYGBKYkfLHUh\","
                        + "            \"type\": \"ESSAY\","
                        + "            \"number\": 4,"
                        + "            \"meta\": \"4\","
                        + "            \"config\": {"
                        + "                \"score\": 12.0,"
                        + "                \"statement\": \"<p>buat program hello world</p>\\r\\n\""
                        + "            }"
                        + "        }"
                        + "    ]"
                        + "}")));

        return mockSandalphon;
    }

    static class TranslateAllowedProblemSlugToJidsTransformer extends ResponseDefinitionTransformer {
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
            return "allowed-problem-slug-to-jid";
        }

        @Override
        public boolean applyGlobally() {
            return false;
        }
    }

    static class TranslateAllowedLessonSlugToJidsTransformer extends ResponseDefinitionTransformer {
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
            for (int i = 0; i < LESSON_SLUGS.length; i++) {
                if (slugs.contains(LESSON_SLUGS[i])) {
                    res.put(LESSON_SLUGS[i], LESSON_JIDS[i]);
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
            return "allowed-lesson-slug-to-jid";
        }

        @Override
        public boolean applyGlobally() {
            return false;
        }
    }
}
