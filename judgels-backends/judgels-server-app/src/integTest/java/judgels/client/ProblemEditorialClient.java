package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import feign.form.FormData;
import judgels.api.catalog.problem.ProblemEditorial;
import judgels.api.catalog.problem.editorial.ProblemEditorialCreateData;
import judgels.api.catalog.problem.editorial.ProblemEditorialLanguagesResponse;
import judgels.api.catalog.problem.editorial.ProblemEditorialMediaFilesResponse;

public interface ProblemEditorialClient {
    @RequestLine("GET /api/v4/problems/{problemJid}/editorial?language={language}")
    @Headers("Authorization: Bearer {token}")
    ProblemEditorial getEditorial(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("language") String language);

    @RequestLine("POST /api/v4/problems/{problemJid}/editorial")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void createEditorial(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            ProblemEditorialCreateData data);

    @RequestLine("PUT /api/v4/problems/{problemJid}/editorial?language={language}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void updateEditorial(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("language") String language,
            ProblemEditorial editorial);

    @RequestLine("GET /api/v4/problems/{problemJid}/editorial/languages")
    @Headers("Authorization: Bearer {token}")
    ProblemEditorialLanguagesResponse getEditorialLanguages(
            @Param("token") String token,
            @Param("problemJid") String problemJid);

    @RequestLine("POST /api/v4/problems/{problemJid}/editorial/languages/{language}")
    @Headers("Authorization: Bearer {token}")
    void addEditorialLanguage(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("language") String language);

    @RequestLine("POST /api/v4/problems/{problemJid}/editorial/languages/{language}/enable")
    @Headers("Authorization: Bearer {token}")
    void enableEditorialLanguage(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("language") String language);

    @RequestLine("POST /api/v4/problems/{problemJid}/editorial/languages/{language}/disable")
    @Headers("Authorization: Bearer {token}")
    void disableEditorialLanguage(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("language") String language);

    @RequestLine("POST /api/v4/problems/{problemJid}/editorial/languages/{language}/make-default")
    @Headers("Authorization: Bearer {token}")
    void makeEditorialLanguageDefault(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("language") String language);

    @RequestLine("GET /api/v4/problems/{problemJid}/editorial/media")
    @Headers("Authorization: Bearer {token}")
    ProblemEditorialMediaFilesResponse getEditorialMediaFiles(
            @Param("token") String token,
            @Param("problemJid") String problemJid);

    @RequestLine("POST /api/v4/problems/{problemJid}/editorial/media")
    @Headers({"Authorization: Bearer {token}", "Content-Type: multipart/form-data"})
    void uploadEditorialMediaFile(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("file") FormData file);

    @RequestLine("POST /api/v4/problems/{problemJid}/editorial/media/zip")
    @Headers({"Authorization: Bearer {token}", "Content-Type: multipart/form-data"})
    void uploadEditorialMediaZip(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("file") FormData file);

    @RequestLine("GET /api/v4/problems/{problemJid}/editorial/media/{filename}")
    @Headers("Authorization: Bearer {token}")
    Response downloadEditorialMediaFile(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("filename") String filename);

    @RequestLine("DELETE /api/v4/problems/{problemJid}/editorial/media/{filename}")
    @Headers("Authorization: Bearer {token}")
    void deleteEditorialMediaFile(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("filename") String filename);

    @RequestLine("DELETE /api/v4/problems/{problemJid}/editorial/media")
    @Headers("Authorization: Bearer {token}")
    void deleteEditorialMediaFiles(@Param("token") String token, @Param("problemJid") String problemJid);
}
