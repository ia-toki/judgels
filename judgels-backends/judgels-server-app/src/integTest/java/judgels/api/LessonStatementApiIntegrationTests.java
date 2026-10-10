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
import judgels.api.catalog.lesson.Lesson;
import judgels.api.catalog.lesson.LessonStatement;
import judgels.api.catalog.lesson.statement.LessonStatementLanguagesResponse;
import judgels.client.LessonClient;
import judgels.client.LessonStatementClient;
import org.junit.jupiter.api.Test;

class LessonStatementApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final LessonClient lessonClient = createClient(LessonClient.class);
    private final LessonStatementClient statementClient = createClient(LessonStatementClient.class);

    @Test
    void update_statement() {
        Lesson lesson = createLesson(adminToken, "statement-lesson");
        String lessonJid = lesson.getJid();

        LessonStatement defaultStatement = statementClient.getStatement(adminToken, lessonJid, null);
        assertThat(defaultStatement.getTitle()).isNotEmpty();
        assertThat(statementClient.getStatement(adminToken, lessonJid, "en-US")).isEqualTo(defaultStatement);

        assertThat(lessonClient.getLesson(adminToken, lessonJid).getHasLocalChanges()).isFalse();

        LessonStatement statement = new LessonStatement.Builder()
                .title("Lesson A")
                .text("<p>Read this.</p>")
                .build();
        statementClient.updateStatement(adminToken, lessonJid, "en-US", statement);

        assertThat(statementClient.getStatement(adminToken, lessonJid, "en-US")).isEqualTo(statement);
        assertThat(lessonClient.getLesson(adminToken, lessonJid).getHasLocalChanges()).isTrue();

        assertBadRequest(() -> statementClient.getStatement(adminToken, lessonJid, "id-ID"));
        assertBadRequest(() -> statementClient.updateStatement(adminToken, lessonJid, "id-ID", statement));

        assertForbidden(() -> statementClient.getStatement(userToken, lessonJid, "en-US"));
        assertForbidden(() -> statementClient.updateStatement(userToken, lessonJid, "en-US", statement));
    }

    @Test
    void manage_statement_languages() {
        Lesson lesson = createLesson(adminToken, "statement-languages-lesson");
        String lessonJid = lesson.getJid();

        LessonStatementLanguagesResponse response = statementClient.getStatementLanguages(adminToken, lessonJid);
        assertThat(response.getEnabledLanguages()).containsExactly("en-US");
        assertThat(response.getDisabledLanguages()).isEmpty();
        assertThat(response.getDefaultLanguage()).isEqualTo("en-US");

        LessonStatement statement = statementClient.getStatement(adminToken, lessonJid, "en-US");

        statementClient.addStatementLanguage(adminToken, lessonJid, "id-ID");
        assertThat(statementClient.getStatement(adminToken, lessonJid, "id-ID")).isEqualTo(statement);

        assertBadRequest(() -> statementClient.addStatementLanguage(adminToken, lessonJid, "id-ID"));
        assertBadRequest(() -> statementClient.addStatementLanguage(adminToken, lessonJid, "bogus"));

        statementClient.disableStatementLanguage(adminToken, lessonJid, "id-ID");
        response = statementClient.getStatementLanguages(adminToken, lessonJid);
        assertThat(response.getEnabledLanguages()).containsExactly("en-US");
        assertThat(response.getDisabledLanguages()).containsExactly("id-ID");

        assertBadRequest(() -> statementClient.getStatement(adminToken, lessonJid, "id-ID"));
        assertBadRequest(() -> statementClient.makeStatementLanguageDefault(adminToken, lessonJid, "id-ID"));
        assertBadRequest(() -> statementClient.disableStatementLanguage(adminToken, lessonJid, "en-US"));
        assertBadRequest(() -> statementClient.enableStatementLanguage(adminToken, lessonJid, "fr-FR"));

        statementClient.enableStatementLanguage(adminToken, lessonJid, "id-ID");
        statementClient.makeStatementLanguageDefault(adminToken, lessonJid, "id-ID");
        response = statementClient.getStatementLanguages(adminToken, lessonJid);
        assertThat(response.getEnabledLanguages()).containsExactlyInAnyOrder("en-US", "id-ID");
        assertThat(response.getDisabledLanguages()).isEmpty();
        assertThat(response.getDefaultLanguage()).isEqualTo("id-ID");

        assertForbidden(() -> statementClient.getStatementLanguages(userToken, lessonJid));
        assertForbidden(() -> statementClient.addStatementLanguage(userToken, lessonJid, "fr-FR"));
        assertForbidden(() -> statementClient.enableStatementLanguage(userToken, lessonJid, "id-ID"));
        assertForbidden(() -> statementClient.disableStatementLanguage(userToken, lessonJid, "en-US"));
        assertForbidden(() -> statementClient.makeStatementLanguageDefault(userToken, lessonJid, "en-US"));
    }

    @Test
    void manage_statement_media_files() throws IOException {
        Lesson lesson = createLesson(adminToken, "statement-media-lesson");
        String lessonJid = lesson.getJid();

        assertThat(statementClient.getStatementMediaFiles(adminToken, lessonJid).getData()).isEmpty();

        statementClient.uploadStatementMediaFile(adminToken, lessonJid, file("figure.png", "FIGURE"));
        statementClient.uploadStatementMediaZip(adminToken, lessonJid, new FormData(
                MULTIPART_FORM_DATA,
                "media.zip",
                zip(Map.of("sample.in", "1 2", "sample.out", "3"))));

        assertThat(statementClient.getStatementMediaFiles(adminToken, lessonJid).getData())
                .extracting(CatalogFile::getName)
                .containsExactly("figure.png", "sample.in", "sample.out");

        try (var response = statementClient.downloadStatementMediaFile(adminToken, lessonJid, "figure.png")) {
            assertThat(response.status()).isEqualTo(200);
            assertThat(response.body().asInputStream().readAllBytes()).isEqualTo("FIGURE".getBytes());
        }

        assertBadRequest(() -> statementClient.uploadStatementMediaFile(adminToken, lessonJid, file(".hidden", "X")));

        statementClient.deleteStatementMediaFile(adminToken, lessonJid, "sample.in");
        assertThat(statementClient.getStatementMediaFiles(adminToken, lessonJid).getData())
                .extracting(CatalogFile::getName)
                .containsExactly("figure.png", "sample.out");

        statementClient.deleteStatementMediaFiles(adminToken, lessonJid);
        assertThat(statementClient.getStatementMediaFiles(adminToken, lessonJid).getData()).isEmpty();

        assertForbidden(() -> statementClient.getStatementMediaFiles(userToken, lessonJid));
        assertForbidden(() -> statementClient.uploadStatementMediaFile(userToken, lessonJid, file("a.png", "A")));
        assertForbidden(() -> statementClient.deleteStatementMediaFile(userToken, lessonJid, "figure.png"));
        assertForbidden(() -> statementClient.deleteStatementMediaFiles(userToken, lessonJid));
        try (var response = statementClient.downloadStatementMediaFile(userToken, lessonJid, "figure.png")) {
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
