package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import feign.form.FormData;
import judgels.api.catalog.problem.ProblemStatement;
import judgels.api.catalog.problem.statement.ProblemStatementLanguagesResponse;
import judgels.api.catalog.problem.statement.ProblemStatementMediaFilesResponse;

public interface ProblemStatementClient {
    @RequestLine("GET /api/v4/problems/{problemJid}/statement?language={language}")
    @Headers("Authorization: Bearer {token}")
    ProblemStatement getStatement(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("language") String language);

    @RequestLine("POST /api/v4/problems/{problemJid}/statement?language={language}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void updateStatement(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("language") String language,
            ProblemStatement statement);

    @RequestLine("GET /api/v4/problems/{problemJid}/statement/languages")
    @Headers("Authorization: Bearer {token}")
    ProblemStatementLanguagesResponse getStatementLanguages(
            @Param("token") String token,
            @Param("problemJid") String problemJid);

    @RequestLine("POST /api/v4/problems/{problemJid}/statement/languages/{language}")
    @Headers("Authorization: Bearer {token}")
    void addStatementLanguage(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("language") String language);

    @RequestLine("POST /api/v4/problems/{problemJid}/statement/languages/{language}/enable")
    @Headers("Authorization: Bearer {token}")
    void enableStatementLanguage(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("language") String language);

    @RequestLine("POST /api/v4/problems/{problemJid}/statement/languages/{language}/disable")
    @Headers("Authorization: Bearer {token}")
    void disableStatementLanguage(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("language") String language);

    @RequestLine("POST /api/v4/problems/{problemJid}/statement/languages/{language}/make-default")
    @Headers("Authorization: Bearer {token}")
    void makeStatementLanguageDefault(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("language") String language);

    @RequestLine("GET /api/v4/problems/{problemJid}/statement/media")
    @Headers("Authorization: Bearer {token}")
    ProblemStatementMediaFilesResponse getStatementMediaFiles(
            @Param("token") String token,
            @Param("problemJid") String problemJid);

    @RequestLine("POST /api/v4/problems/{problemJid}/statement/media")
    @Headers({"Authorization: Bearer {token}", "Content-Type: multipart/form-data"})
    void uploadStatementMediaFile(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("file") FormData file);

    @RequestLine("POST /api/v4/problems/{problemJid}/statement/media/zip")
    @Headers({"Authorization: Bearer {token}", "Content-Type: multipart/form-data"})
    void uploadStatementMediaZip(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("file") FormData file);

    @RequestLine("GET /api/v4/problems/{problemJid}/statement/media/{filename}")
    @Headers("Authorization: Bearer {token}")
    Response downloadStatementMediaFile(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("filename") String filename);

    @RequestLine("DELETE /api/v4/problems/{problemJid}/statement/media/{filename}")
    @Headers("Authorization: Bearer {token}")
    void deleteStatementMediaFile(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("filename") String filename);

    @RequestLine("DELETE /api/v4/problems/{problemJid}/statement/media")
    @Headers("Authorization: Bearer {token}")
    void deleteStatementMediaFiles(@Param("token") String token, @Param("problemJid") String problemJid);
}
