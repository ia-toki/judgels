package judgels.api;

import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import feign.form.FormData;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.CatalogFile;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.programming.grading.ProblemGradingConfig;
import judgels.api.catalog.problem.programming.grading.ProblemGradingEngineUpdateData;
import judgels.client.ProblemClient;
import judgels.client.ProblemGradingClient;
import judgels.grading.api.LanguageRestriction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProblemGradingApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final ProblemClient problemClient = createClient(ProblemClient.class);
    private final ProblemGradingClient gradingClient = createClient(ProblemGradingClient.class);

    @BeforeAll
    static void setUpWebTarget() {
        webTarget = createWebTarget();
    }

    @Test
    void update_grading_engine_and_config() throws IOException {
        Problem problem = createProblem(adminToken, "grading-config-problem");
        String problemJid = problem.getJid();

        ProblemGradingConfig gradingConfig = gradingClient.getGradingConfig(adminToken, problemJid);
        assertThat(gradingConfig.getEngine()).isEqualTo("Batch");
        assertThat(problemClient.getProblem(adminToken, problemJid).getHasLocalChanges()).isFalse();

        ObjectNode config = gradingConfig.getConfig().deepCopy();
        config.put("timeLimit", 3000);
        config.set("testData", MAPPER.readTree("["
                + "{\"id\":0,\"testCases\":[{\"input\":\"sample_1.in\",\"output\":\"sample_1.out\",\"subtaskIds\":[0]}]},"
                + "{\"id\":-1,\"testCases\":[{\"input\":\"1.in\",\"output\":\"1.out\",\"subtaskIds\":[-1]}]}]"));
        ProblemGradingConfig newGradingConfig = new ProblemGradingConfig.Builder().engine("Batch").config(config).build();
        gradingClient.updateGradingConfig(adminToken, problemJid, newGradingConfig);

        assertThat(gradingClient.getGradingConfig(adminToken, problemJid)).isEqualTo(newGradingConfig);
        assertThat(problemClient.getProblem(adminToken, problemJid).getHasLocalChanges()).isTrue();

        // a config for another engine, and a config the engine cannot parse
        assertBadRequest(() -> gradingClient.updateGradingConfig(adminToken, problemJid, new ProblemGradingConfig.Builder()
                .engine("Interactive")
                .config(config)
                .build()));
        assertBadRequest(() -> gradingClient.updateGradingConfig(adminToken, problemJid, new ProblemGradingConfig.Builder()
                .engine("Batch")
                .config(MAPPER.readTree("{\"timeLimit\":\"fast\"}"))
                .build()));

        // setting the same engine keeps the config
        gradingClient.updateGradingEngine(adminToken, problemJid, engine("Batch"));
        assertThat(gradingClient.getGradingConfig(adminToken, problemJid)).isEqualTo(newGradingConfig);

        gradingClient.updateGradingEngine(adminToken, problemJid, engine("BatchWithSubtasks"));
        gradingConfig = gradingClient.getGradingConfig(adminToken, problemJid);
        assertThat(gradingConfig.getEngine()).isEqualTo("BatchWithSubtasks");
        assertThat(gradingConfig.getConfig().get("timeLimit").asInt()).isEqualTo(2000);
        assertThat(gradingConfig.getConfig().has("subtaskPoints")).isTrue();

        assertBadRequest(() -> gradingClient.updateGradingEngine(adminToken, problemJid, engine("Bogus")));

        assertForbidden(() -> gradingClient.getGradingConfig(userToken, problemJid));
        assertForbidden(() -> gradingClient.updateGradingConfig(userToken, problemJid, newGradingConfig));
        assertForbidden(() -> gradingClient.updateGradingEngine(userToken, problemJid, engine("Batch")));
        assertForbidden(() -> gradingClient.autoPopulateGradingConfig(userToken, problemJid));

        Problem bundleProblem = createBundleProblem(adminToken, "grading-bundle-problem");
        assertNotFound(() -> gradingClient.getGradingConfig(adminToken, bundleProblem.getJid()));
    }

    @Test
    void auto_populate_grading_config() throws IOException {
        Problem problem = createProblem(adminToken, "grading-auto-populate-problem");
        String problemJid = problem.getJid();

        gradingClient.uploadGradingTestDataZip(adminToken, problemJid, new FormData(
                MULTIPART_FORM_DATA,
                "testdata.zip",
                zip(Map.of("sample_1.in", "1", "sample_1.out", "1", "1.in", "2", "1.out", "2"))));

        JsonNode before = gradingClient.getGradingConfig(adminToken, problemJid).getConfig();

        ProblemGradingConfig proposed = gradingClient.autoPopulateGradingConfig(adminToken, problemJid);
        assertThat(proposed.getEngine()).isEqualTo("Batch");
        assertThat(proposed.getConfig().get("testData")).isEqualTo(MAPPER.readTree("["
                + "{\"id\":0,\"testCases\":[{\"input\":\"sample_1.in\",\"output\":\"sample_1.out\",\"subtaskIds\":[0]}]},"
                + "{\"id\":-1,\"testCases\":[{\"input\":\"1.in\",\"output\":\"1.out\",\"subtaskIds\":[-1]}]}]"));

        // the proposal is not saved
        assertThat(gradingClient.getGradingConfig(adminToken, problemJid).getConfig()).isEqualTo(before);
    }

    @Test
    void manage_grading_test_data_files() throws IOException {
        Problem problem = createProblem(adminToken, "grading-test-data-problem");
        String problemJid = problem.getJid();

        assertThat(gradingClient.getGradingTestDataFiles(adminToken, problemJid).getData()).isEmpty();

        gradingClient.uploadGradingTestDataFile(adminToken, problemJid, file("1.in", "INPUT"));
        gradingClient.uploadGradingTestDataZip(adminToken, problemJid, new FormData(
                MULTIPART_FORM_DATA,
                "testdata.zip",
                zip(Map.of("1.out", "OUTPUT", "2.in", "INPUT"))));

        assertThat(gradingClient.getGradingTestDataFiles(adminToken, problemJid).getData())
                .extracting(CatalogFile::getName)
                .containsExactly("1.in", "1.out", "2.in");
        assertThat(problemClient.getProblem(adminToken, problemJid).getHasLocalChanges()).isTrue();

        try (var response = gradingClient.downloadGradingTestDataFile(adminToken, problemJid, "1.in")) {
            assertThat(response.status()).isEqualTo(200);
            assertThat(response.body().asInputStream().readAllBytes()).isEqualTo("INPUT".getBytes());
        }

        assertBadRequest(() -> gradingClient.uploadGradingTestDataFile(adminToken, problemJid, file(".hidden", "X")));

        gradingClient.deleteGradingTestDataFile(adminToken, problemJid, "2.in");
        assertThat(gradingClient.getGradingTestDataFiles(adminToken, problemJid).getData())
                .extracting(CatalogFile::getName)
                .containsExactly("1.in", "1.out");

        gradingClient.deleteGradingTestDataFiles(adminToken, problemJid);
        assertThat(gradingClient.getGradingTestDataFiles(adminToken, problemJid).getData()).isEmpty();

        assertForbidden(() -> gradingClient.getGradingTestDataFiles(userToken, problemJid));
        assertForbidden(() -> gradingClient.uploadGradingTestDataFile(userToken, problemJid, file("3.in", "A")));
        assertForbidden(() -> gradingClient.deleteGradingTestDataFile(userToken, problemJid, "1.in"));
        assertForbidden(() -> gradingClient.deleteGradingTestDataFiles(userToken, problemJid));
        try (var response = gradingClient.downloadGradingTestDataFile(userToken, problemJid, "1.in")) {
            assertThat(response.status()).isEqualTo(403);
        }
    }

    @Test
    void manage_grading_helper_files() throws IOException {
        Problem problem = createProblem(adminToken, "grading-helpers-problem");
        String problemJid = problem.getJid();

        assertThat(gradingClient.getGradingHelperFiles(adminToken, problemJid).getData()).isEmpty();

        gradingClient.uploadGradingHelperFile(adminToken, problemJid, file("scorer.cpp", "SCORER"));
        gradingClient.uploadGradingHelperZip(adminToken, problemJid, new FormData(
                MULTIPART_FORM_DATA,
                "helpers.zip",
                zip(Map.of("communicator.cpp", "COMMUNICATOR"))));

        assertThat(gradingClient.getGradingHelperFiles(adminToken, problemJid).getData())
                .extracting(CatalogFile::getName)
                .containsExactly("communicator.cpp", "scorer.cpp");

        try (var response = gradingClient.downloadGradingHelperFile(adminToken, problemJid, "scorer.cpp")) {
            assertThat(response.status()).isEqualTo(200);
            assertThat(response.body().asInputStream().readAllBytes()).isEqualTo("SCORER".getBytes());
        }

        gradingClient.deleteGradingHelperFile(adminToken, problemJid, "communicator.cpp");
        assertThat(gradingClient.getGradingHelperFiles(adminToken, problemJid).getData())
                .extracting(CatalogFile::getName)
                .containsExactly("scorer.cpp");

        gradingClient.deleteGradingHelperFiles(adminToken, problemJid);
        assertThat(gradingClient.getGradingHelperFiles(adminToken, problemJid).getData()).isEmpty();

        assertForbidden(() -> gradingClient.getGradingHelperFiles(userToken, problemJid));
        assertForbidden(() -> gradingClient.uploadGradingHelperFile(userToken, problemJid, file("a.cpp", "A")));
        assertForbidden(() -> gradingClient.deleteGradingHelperFile(userToken, problemJid, "scorer.cpp"));
        assertForbidden(() -> gradingClient.deleteGradingHelperFiles(userToken, problemJid));
    }

    @Test
    void update_grading_language_restriction() {
        Problem problem = createProblem(adminToken, "grading-language-restriction-problem");
        String problemJid = problem.getJid();

        assertThat(gradingClient.getGradingLanguageRestriction(adminToken, problemJid))
                .isEqualTo(LanguageRestriction.noRestriction());

        LanguageRestriction restriction = LanguageRestriction.of(Set.of("Cpp17", "Python3"));
        gradingClient.updateGradingLanguageRestriction(adminToken, problemJid, restriction);
        assertThat(gradingClient.getGradingLanguageRestriction(adminToken, problemJid)).isEqualTo(restriction);

        assertBadRequest(() -> gradingClient.updateGradingLanguageRestriction(
                adminToken,
                problemJid,
                LanguageRestriction.of(Set.of("Bogus"))));

        assertForbidden(() -> gradingClient.getGradingLanguageRestriction(userToken, problemJid));
        assertForbidden(() -> gradingClient.updateGradingLanguageRestriction(userToken, problemJid, restriction));
    }

    private static ProblemGradingEngineUpdateData engine(String engine) {
        return new ProblemGradingEngineUpdateData.Builder().engine(engine).build();
    }

    private static FormData file(String filename, String content) {
        return new FormData(MULTIPART_FORM_DATA, filename, content.getBytes());
    }

    private static byte[] zip(Map<String, String> files) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(bytes)) {
            for (Map.Entry<String, String> file : files.entrySet()) {
                zip.putNextEntry(new ZipEntry(file.getKey()));
                zip.write(file.getValue().getBytes());
                zip.closeEntry();
            }
        }
        return bytes.toByteArray();
    }
}
