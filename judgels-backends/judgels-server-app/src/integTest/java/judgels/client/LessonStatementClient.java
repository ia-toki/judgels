package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import feign.form.FormData;
import judgels.api.catalog.lesson.LessonStatement;
import judgels.api.catalog.lesson.statement.LessonStatementLanguagesResponse;
import judgels.api.catalog.lesson.statement.LessonStatementMediaFilesResponse;

public interface LessonStatementClient {
    @RequestLine("GET /api/v4/lessons/{lessonJid}/statement?language={language}")
    @Headers("Authorization: Bearer {token}")
    LessonStatement getStatement(
            @Param("token") String token,
            @Param("lessonJid") String lessonJid,
            @Param("language") String language);

    @RequestLine("POST /api/v4/lessons/{lessonJid}/statement?language={language}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void updateStatement(
            @Param("token") String token,
            @Param("lessonJid") String lessonJid,
            @Param("language") String language,
            LessonStatement statement);

    @RequestLine("GET /api/v4/lessons/{lessonJid}/statement/languages")
    @Headers("Authorization: Bearer {token}")
    LessonStatementLanguagesResponse getStatementLanguages(
            @Param("token") String token,
            @Param("lessonJid") String lessonJid);

    @RequestLine("POST /api/v4/lessons/{lessonJid}/statement/languages/{language}")
    @Headers("Authorization: Bearer {token}")
    void addStatementLanguage(
            @Param("token") String token,
            @Param("lessonJid") String lessonJid,
            @Param("language") String language);

    @RequestLine("POST /api/v4/lessons/{lessonJid}/statement/languages/{language}/enable")
    @Headers("Authorization: Bearer {token}")
    void enableStatementLanguage(
            @Param("token") String token,
            @Param("lessonJid") String lessonJid,
            @Param("language") String language);

    @RequestLine("POST /api/v4/lessons/{lessonJid}/statement/languages/{language}/disable")
    @Headers("Authorization: Bearer {token}")
    void disableStatementLanguage(
            @Param("token") String token,
            @Param("lessonJid") String lessonJid,
            @Param("language") String language);

    @RequestLine("POST /api/v4/lessons/{lessonJid}/statement/languages/{language}/make-default")
    @Headers("Authorization: Bearer {token}")
    void makeStatementLanguageDefault(
            @Param("token") String token,
            @Param("lessonJid") String lessonJid,
            @Param("language") String language);

    @RequestLine("GET /api/v4/lessons/{lessonJid}/statement/media")
    @Headers("Authorization: Bearer {token}")
    LessonStatementMediaFilesResponse getStatementMediaFiles(
            @Param("token") String token,
            @Param("lessonJid") String lessonJid);

    @RequestLine("POST /api/v4/lessons/{lessonJid}/statement/media")
    @Headers({"Authorization: Bearer {token}", "Content-Type: multipart/form-data"})
    void uploadStatementMediaFile(
            @Param("token") String token,
            @Param("lessonJid") String lessonJid,
            @Param("file") FormData file);

    @RequestLine("POST /api/v4/lessons/{lessonJid}/statement/media/zip")
    @Headers({"Authorization: Bearer {token}", "Content-Type: multipart/form-data"})
    void uploadStatementMediaZip(
            @Param("token") String token,
            @Param("lessonJid") String lessonJid,
            @Param("file") FormData file);

    @RequestLine("GET /api/v4/lessons/{lessonJid}/statement/media/{filename}")
    @Headers("Authorization: Bearer {token}")
    Response downloadStatementMediaFile(
            @Param("token") String token,
            @Param("lessonJid") String lessonJid,
            @Param("filename") String filename);

    @RequestLine("DELETE /api/v4/lessons/{lessonJid}/statement/media/{filename}")
    @Headers("Authorization: Bearer {token}")
    void deleteStatementMediaFile(
            @Param("token") String token,
            @Param("lessonJid") String lessonJid,
            @Param("filename") String filename);

    @RequestLine("DELETE /api/v4/lessons/{lessonJid}/statement/media")
    @Headers("Authorization: Bearer {token}")
    void deleteStatementMediaFiles(@Param("token") String token, @Param("lessonJid") String lessonJid);
}
