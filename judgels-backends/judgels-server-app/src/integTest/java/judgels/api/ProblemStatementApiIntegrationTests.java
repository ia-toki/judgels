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
import judgels.api.catalog.CatalogFile;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.ProblemStatement;
import judgels.api.catalog.problem.statement.ProblemStatementLanguagesResponse;
import judgels.client.ProblemClient;
import judgels.client.ProblemStatementClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProblemStatementApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final ProblemClient problemClient = createClient(ProblemClient.class);
    private final ProblemStatementClient statementClient = createClient(ProblemStatementClient.class);

    @BeforeAll
    static void setUpWebTarget() {
        webTarget = createWebTarget();
    }

    @Test
    void update_statement() {
        Problem problem = createProblem(adminToken, "statement-problem");
        String problemJid = problem.getJid();

        ProblemStatement defaultStatement = statementClient.getStatement(adminToken, problemJid, null);
        assertThat(defaultStatement.getTitle()).isNotEmpty();
        assertThat(statementClient.getStatement(adminToken, problemJid, "en-US")).isEqualTo(defaultStatement);

        assertThat(problemClient.getProblem(adminToken, problemJid).getHasLocalChanges()).isFalse();

        ProblemStatement statement = new ProblemStatement.Builder()
                .title("Problem A")
                .text("<p>Find the answer.</p>")
                .build();
        statementClient.updateStatement(adminToken, problemJid, "en-US", statement);

        assertThat(statementClient.getStatement(adminToken, problemJid, "en-US")).isEqualTo(statement);
        assertThat(problemClient.getProblem(adminToken, problemJid).getHasLocalChanges()).isTrue();

        assertBadRequest(() -> statementClient.getStatement(adminToken, problemJid, "id-ID"));
        assertBadRequest(() -> statementClient.updateStatement(adminToken, problemJid, "id-ID", statement));

        assertForbidden(() -> statementClient.getStatement(userToken, problemJid, "en-US"));
        assertForbidden(() -> statementClient.updateStatement(userToken, problemJid, "en-US", statement));
    }

    @Test
    void manage_statement_languages() {
        Problem problem = createProblem(adminToken, "statement-languages-problem");
        String problemJid = problem.getJid();

        ProblemStatementLanguagesResponse response = statementClient.getStatementLanguages(adminToken, problemJid);
        assertThat(response.getEnabledLanguages()).containsExactly("en-US");
        assertThat(response.getDisabledLanguages()).isEmpty();
        assertThat(response.getDefaultLanguage()).isEqualTo("en-US");

        ProblemStatement statement = statementClient.getStatement(adminToken, problemJid, "en-US");

        statementClient.addStatementLanguage(adminToken, problemJid, "id-ID");
        assertThat(statementClient.getStatement(adminToken, problemJid, "id-ID")).isEqualTo(statement);

        assertBadRequest(() -> statementClient.addStatementLanguage(adminToken, problemJid, "id-ID"));
        assertBadRequest(() -> statementClient.addStatementLanguage(adminToken, problemJid, "bogus"));

        statementClient.disableStatementLanguage(adminToken, problemJid, "id-ID");
        response = statementClient.getStatementLanguages(adminToken, problemJid);
        assertThat(response.getEnabledLanguages()).containsExactly("en-US");
        assertThat(response.getDisabledLanguages()).containsExactly("id-ID");

        assertBadRequest(() -> statementClient.getStatement(adminToken, problemJid, "id-ID"));
        assertBadRequest(() -> statementClient.makeStatementLanguageDefault(adminToken, problemJid, "id-ID"));
        assertBadRequest(() -> statementClient.disableStatementLanguage(adminToken, problemJid, "en-US"));
        assertBadRequest(() -> statementClient.enableStatementLanguage(adminToken, problemJid, "fr-FR"));

        statementClient.enableStatementLanguage(adminToken, problemJid, "id-ID");
        statementClient.makeStatementLanguageDefault(adminToken, problemJid, "id-ID");
        response = statementClient.getStatementLanguages(adminToken, problemJid);
        assertThat(response.getEnabledLanguages()).containsExactlyInAnyOrder("en-US", "id-ID");
        assertThat(response.getDisabledLanguages()).isEmpty();
        assertThat(response.getDefaultLanguage()).isEqualTo("id-ID");

        assertForbidden(() -> statementClient.getStatementLanguages(userToken, problemJid));
        assertForbidden(() -> statementClient.addStatementLanguage(userToken, problemJid, "fr-FR"));
        assertForbidden(() -> statementClient.enableStatementLanguage(userToken, problemJid, "id-ID"));
        assertForbidden(() -> statementClient.disableStatementLanguage(userToken, problemJid, "en-US"));
        assertForbidden(() -> statementClient.makeStatementLanguageDefault(userToken, problemJid, "en-US"));
    }

    @Test
    void manage_statement_media_files() throws IOException {
        Problem problem = createProblem(adminToken, "statement-media-problem");
        String problemJid = problem.getJid();

        assertThat(statementClient.getStatementMediaFiles(adminToken, problemJid).getData()).isEmpty();

        statementClient.uploadStatementMediaFile(adminToken, problemJid, file("figure.png", "FIGURE"));
        statementClient.uploadStatementMediaZip(adminToken, problemJid, new FormData(
                MULTIPART_FORM_DATA,
                "media.zip",
                zip(Map.of("sample.in", "1 2", "sample.out", "3"))));

        assertThat(statementClient.getStatementMediaFiles(adminToken, problemJid).getData())
                .extracting(CatalogFile::getName)
                .containsExactly("figure.png", "sample.in", "sample.out");

        try (var response = statementClient.downloadStatementMediaFile(adminToken, problemJid, "figure.png")) {
            assertThat(response.status()).isEqualTo(200);
            assertThat(response.body().asInputStream().readAllBytes()).isEqualTo("FIGURE".getBytes());
        }

        assertBadRequest(() -> statementClient.uploadStatementMediaFile(adminToken, problemJid, file(".hidden", "X")));

        statementClient.deleteStatementMediaFile(adminToken, problemJid, "sample.in");
        assertThat(statementClient.getStatementMediaFiles(adminToken, problemJid).getData())
                .extracting(CatalogFile::getName)
                .containsExactly("figure.png", "sample.out");

        statementClient.deleteStatementMediaFiles(adminToken, problemJid);
        assertThat(statementClient.getStatementMediaFiles(adminToken, problemJid).getData()).isEmpty();

        assertForbidden(() -> statementClient.getStatementMediaFiles(userToken, problemJid));
        assertForbidden(() -> statementClient.uploadStatementMediaFile(userToken, problemJid, file("a.png", "A")));
        assertForbidden(() -> statementClient.deleteStatementMediaFile(userToken, problemJid, "figure.png"));
        assertForbidden(() -> statementClient.deleteStatementMediaFiles(userToken, problemJid));
        try (var response = statementClient.downloadStatementMediaFile(userToken, problemJid, "figure.png")) {
            assertThat(response.status()).isEqualTo(403);
        }
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
