package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.catalog.problem.version.ProblemVersionCommitData;
import judgels.api.catalog.problem.version.ProblemVersionsResponse;

public interface ProblemVersionClient {
    @RequestLine("GET /api/v4/problems/{problemJid}/versions")
    @Headers("Authorization: Bearer {token}")
    ProblemVersionsResponse getVersions(@Param("token") String token, @Param("problemJid") String problemJid);

    @RequestLine("POST /api/v4/problems/{problemJid}/versions/{versionHash}/restore")
    @Headers("Authorization: Bearer {token}")
    void restoreVersion(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            @Param("versionHash") String versionHash);

    @RequestLine("POST /api/v4/problems/{problemJid}/versions/local/commit")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void commitVersionLocalChanges(
            @Param("token") String token,
            @Param("problemJid") String problemJid,
            ProblemVersionCommitData data);

    @RequestLine("POST /api/v4/problems/{problemJid}/versions/local/rebase")
    @Headers("Authorization: Bearer {token}")
    void rebaseVersionLocalChanges(@Param("token") String token, @Param("problemJid") String problemJid);

    @RequestLine("DELETE /api/v4/problems/{problemJid}/versions/local")
    @Headers("Authorization: Bearer {token}")
    void discardVersionLocalChanges(@Param("token") String token, @Param("problemJid") String problemJid);
}
