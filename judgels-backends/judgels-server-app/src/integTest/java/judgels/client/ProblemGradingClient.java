package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import feign.form.FormData;
import judgels.api.catalog.problem.programming.grading.ProblemGradingConfig;
import judgels.api.catalog.problem.programming.grading.ProblemGradingEngineUpdateData;
import judgels.api.catalog.problem.programming.grading.ProblemGradingFilesResponse;
import judgels.grading.api.LanguageRestriction;

public interface ProblemGradingClient {
    @RequestLine("PUT /api/v4/problems/{problemJid}/grading/engine")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void updateGradingEngine(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            ProblemGradingEngineUpdateData data);

    @RequestLine("GET /api/v4/problems/{problemJid}/grading/config")
    @Headers("Authorization: Bearer {token}")
    ProblemGradingConfig getGradingConfig(@Param("token") String token, @Param("problemJid") String problemJid);

    @RequestLine("PUT /api/v4/problems/{problemJid}/grading/config")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void updateGradingConfig(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            ProblemGradingConfig data);

    @RequestLine("POST /api/v4/problems/{problemJid}/grading/config/auto-populate")
    @Headers("Authorization: Bearer {token}")
    ProblemGradingConfig autoPopulateGradingConfig(
            @Param("token") String token,
            @Param("problemJid") String problemJid);

    @RequestLine("GET /api/v4/problems/{problemJid}/grading/test-data")
    @Headers("Authorization: Bearer {token}")
    ProblemGradingFilesResponse getGradingTestDataFiles(
            @Param("token") String token,
            @Param("problemJid") String problemJid);

    @RequestLine("POST /api/v4/problems/{problemJid}/grading/test-data")
    @Headers({"Authorization: Bearer {token}", "Content-Type: multipart/form-data"})
    void uploadGradingTestDataFile(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("file") FormData file);

    @RequestLine("POST /api/v4/problems/{problemJid}/grading/test-data/zip")
    @Headers({"Authorization: Bearer {token}", "Content-Type: multipart/form-data"})
    void uploadGradingTestDataZip(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("file") FormData file);

    @RequestLine("GET /api/v4/problems/{problemJid}/grading/test-data/{filename}")
    @Headers("Authorization: Bearer {token}")
    Response downloadGradingTestDataFile(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("filename") String filename);

    @RequestLine("DELETE /api/v4/problems/{problemJid}/grading/test-data/{filename}")
    @Headers("Authorization: Bearer {token}")
    void deleteGradingTestDataFile(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("filename") String filename);

    @RequestLine("DELETE /api/v4/problems/{problemJid}/grading/test-data")
    @Headers("Authorization: Bearer {token}")
    void deleteGradingTestDataFiles(@Param("token") String token, @Param("problemJid") String problemJid);

    @RequestLine("GET /api/v4/problems/{problemJid}/grading/helpers")
    @Headers("Authorization: Bearer {token}")
    ProblemGradingFilesResponse getGradingHelperFiles(
            @Param("token") String token,
            @Param("problemJid") String problemJid);

    @RequestLine("POST /api/v4/problems/{problemJid}/grading/helpers")
    @Headers({"Authorization: Bearer {token}", "Content-Type: multipart/form-data"})
    void uploadGradingHelperFile(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("file") FormData file);

    @RequestLine("POST /api/v4/problems/{problemJid}/grading/helpers/zip")
    @Headers({"Authorization: Bearer {token}", "Content-Type: multipart/form-data"})
    void uploadGradingHelperZip(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("file") FormData file);

    @RequestLine("GET /api/v4/problems/{problemJid}/grading/helpers/{filename}")
    @Headers("Authorization: Bearer {token}")
    Response downloadGradingHelperFile(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("filename") String filename);

    @RequestLine("DELETE /api/v4/problems/{problemJid}/grading/helpers/{filename}")
    @Headers("Authorization: Bearer {token}")
    void deleteGradingHelperFile(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("filename") String filename);

    @RequestLine("DELETE /api/v4/problems/{problemJid}/grading/helpers")
    @Headers("Authorization: Bearer {token}")
    void deleteGradingHelperFiles(@Param("token") String token, @Param("problemJid") String problemJid);

    @RequestLine("GET /api/v4/problems/{problemJid}/grading/language-restriction")
    @Headers("Authorization: Bearer {token}")
    LanguageRestriction getGradingLanguageRestriction(
            @Param("token") String token,
            @Param("problemJid") String problemJid);

    @RequestLine("PUT /api/v4/problems/{problemJid}/grading/language-restriction")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void updateGradingLanguageRestriction(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            LanguageRestriction languageRestriction);
}
