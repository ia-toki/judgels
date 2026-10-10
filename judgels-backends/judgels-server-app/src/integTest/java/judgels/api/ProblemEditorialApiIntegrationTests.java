package judgels.api;

import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.assertj.core.api.Assertions.assertThat;

import feign.form.FormData;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.ProblemEditorial;
import judgels.api.catalog.problem.ProblemFile;
import judgels.api.catalog.problem.editorial.ProblemEditorialCreateData;
import judgels.api.catalog.problem.editorial.ProblemEditorialLanguagesResponse;
import judgels.client.ProblemClient;
import judgels.client.ProblemEditorialClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProblemEditorialApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final ProblemClient problemClient = createClient(ProblemClient.class);
    private final ProblemEditorialClient editorialClient = createClient(ProblemEditorialClient.class);

    @BeforeAll
    static void setUpWebTarget() {
        webTarget = createWebTarget();
    }

    @Test
    void create_update_editorial() {
        Problem problem = createProblem(adminToken, "editorial-problem");
        String problemJid = problem.getJid();

        assertThat(problemClient.getProblem(adminToken, problemJid).getHasEditorial()).isFalse();
        assertNotFound(() -> editorialClient.getEditorial(adminToken, problemJid, null));
        assertNotFound(() -> editorialClient.getEditorialLanguages(adminToken, problemJid));
        assertNotFound(() -> editorialClient.getEditorialMediaFiles(adminToken, problemJid));
        assertNotFound(() -> editorialClient.updateEditorial(adminToken, problemJid, "en-US", editorial("<p>X</p>")));

        assertBadRequest(() -> editorialClient.createEditorial(adminToken, problemJid, createData("bogus")));
        assertForbidden(() -> editorialClient.createEditorial(userToken, problemJid, createData("en-US")));

        editorialClient.createEditorial(adminToken, problemJid, createData("en-US"));

        assertThat(problemClient.getProblem(adminToken, problemJid).getHasEditorial()).isTrue();
        assertThat(problemClient.getProblem(adminToken, problemJid).getHasLocalChanges()).isTrue();
        assertThat(editorialClient.getEditorial(adminToken, problemJid, null)).isEqualTo(editorial(""));

        assertBadRequest(() -> editorialClient.createEditorial(adminToken, problemJid, createData("en-US")));

        ProblemEditorial editorial = editorial("<p>Sort the array.</p>");
        editorialClient.updateEditorial(adminToken, problemJid, "en-US", editorial);

        assertThat(editorialClient.getEditorial(adminToken, problemJid, "en-US")).isEqualTo(editorial);
        assertThat(editorialClient.getEditorial(adminToken, problemJid, null)).isEqualTo(editorial);

        assertBadRequest(() -> editorialClient.getEditorial(adminToken, problemJid, "id-ID"));
        assertBadRequest(() -> editorialClient.updateEditorial(adminToken, problemJid, "id-ID", editorial));

        assertForbidden(() -> editorialClient.getEditorial(userToken, problemJid, "en-US"));
        assertForbidden(() -> editorialClient.updateEditorial(userToken, problemJid, "en-US", editorial));
    }

    @Test
    void manage_editorial_languages() {
        Problem problem = createProblem(adminToken, "editorial-languages-problem");
        String problemJid = problem.getJid();

        editorialClient.createEditorial(adminToken, problemJid, createData("en-US"));

        ProblemEditorialLanguagesResponse response = editorialClient.getEditorialLanguages(adminToken, problemJid);
        assertThat(response.getEnabledLanguages()).containsExactly("en-US");
        assertThat(response.getDisabledLanguages()).isEmpty();
        assertThat(response.getDefaultLanguage()).isEqualTo("en-US");

        editorialClient.updateEditorial(adminToken, problemJid, "en-US", editorial("<p>Sort the array.</p>"));

        editorialClient.addEditorialLanguage(adminToken, problemJid, "id-ID");
        assertThat(editorialClient.getEditorial(adminToken, problemJid, "id-ID")).isEqualTo(editorial(""));

        assertBadRequest(() -> editorialClient.addEditorialLanguage(adminToken, problemJid, "id-ID"));
        assertBadRequest(() -> editorialClient.addEditorialLanguage(adminToken, problemJid, "bogus"));

        editorialClient.disableEditorialLanguage(adminToken, problemJid, "id-ID");
        response = editorialClient.getEditorialLanguages(adminToken, problemJid);
        assertThat(response.getEnabledLanguages()).containsExactly("en-US");
        assertThat(response.getDisabledLanguages()).containsExactly("id-ID");

        assertBadRequest(() -> editorialClient.getEditorial(adminToken, problemJid, "id-ID"));
        assertBadRequest(() -> editorialClient.makeEditorialLanguageDefault(adminToken, problemJid, "id-ID"));
        assertBadRequest(() -> editorialClient.disableEditorialLanguage(adminToken, problemJid, "en-US"));
        assertBadRequest(() -> editorialClient.enableEditorialLanguage(adminToken, problemJid, "fr-FR"));

        editorialClient.enableEditorialLanguage(adminToken, problemJid, "id-ID");
        editorialClient.makeEditorialLanguageDefault(adminToken, problemJid, "id-ID");
        response = editorialClient.getEditorialLanguages(adminToken, problemJid);
        assertThat(response.getEnabledLanguages()).containsExactlyInAnyOrder("en-US", "id-ID");
        assertThat(response.getDisabledLanguages()).isEmpty();
        assertThat(response.getDefaultLanguage()).isEqualTo("id-ID");

        assertForbidden(() -> editorialClient.getEditorialLanguages(userToken, problemJid));
        assertForbidden(() -> editorialClient.addEditorialLanguage(userToken, problemJid, "fr-FR"));
        assertForbidden(() -> editorialClient.enableEditorialLanguage(userToken, problemJid, "id-ID"));
        assertForbidden(() -> editorialClient.disableEditorialLanguage(userToken, problemJid, "en-US"));
        assertForbidden(() -> editorialClient.makeEditorialLanguageDefault(userToken, problemJid, "en-US"));
    }

    @Test
    void manage_editorial_media_files() throws IOException {
        Problem problem = createProblem(adminToken, "editorial-media-problem");
        String problemJid = problem.getJid();

        editorialClient.createEditorial(adminToken, problemJid, createData("en-US"));

        assertThat(editorialClient.getEditorialMediaFiles(adminToken, problemJid).getData()).isEmpty();

        editorialClient.uploadEditorialMediaFile(adminToken, problemJid, file("figure.png", "FIGURE"));
        editorialClient.uploadEditorialMediaZip(adminToken, problemJid, new FormData(
                MULTIPART_FORM_DATA,
                "media.zip",
                zip(Map.of("graph.png", "GRAPH", "tree.png", "TREE"))));

        assertThat(editorialClient.getEditorialMediaFiles(adminToken, problemJid).getData())
                .extracting(ProblemFile::getName)
                .containsExactly("figure.png", "graph.png", "tree.png");

        try (var response = editorialClient.downloadEditorialMediaFile(adminToken, problemJid, "figure.png")) {
            assertThat(response.status()).isEqualTo(200);
            assertThat(response.body().asInputStream().readAllBytes()).isEqualTo("FIGURE".getBytes());
        }

        assertBadRequest(() -> editorialClient.uploadEditorialMediaFile(adminToken, problemJid, file(".hidden", "X")));

        editorialClient.deleteEditorialMediaFile(adminToken, problemJid, "graph.png");
        assertThat(editorialClient.getEditorialMediaFiles(adminToken, problemJid).getData())
                .extracting(ProblemFile::getName)
                .containsExactly("figure.png", "tree.png");

        editorialClient.deleteEditorialMediaFiles(adminToken, problemJid);
        assertThat(editorialClient.getEditorialMediaFiles(adminToken, problemJid).getData()).isEmpty();

        assertForbidden(() -> editorialClient.getEditorialMediaFiles(userToken, problemJid));
        assertForbidden(() -> editorialClient.uploadEditorialMediaFile(userToken, problemJid, file("a.png", "A")));
        assertForbidden(() -> editorialClient.deleteEditorialMediaFile(userToken, problemJid, "figure.png"));
        assertForbidden(() -> editorialClient.deleteEditorialMediaFiles(userToken, problemJid));
        try (var response = editorialClient.downloadEditorialMediaFile(userToken, problemJid, "figure.png")) {
            assertThat(response.status()).isEqualTo(403);
        }
    }

    private static ProblemEditorialCreateData createData(String initialLanguage) {
        return new ProblemEditorialCreateData.Builder().initialLanguage(initialLanguage).build();
    }

    private static ProblemEditorial editorial(String text) {
        return new ProblemEditorial.Builder().text(text).build();
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
