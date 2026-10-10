package judgels.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.catalog.lesson.version.LessonVersionCommitData;
import judgels.api.catalog.lesson.version.LessonVersionsResponse;

public interface LessonVersionClient {
    @RequestLine("GET /api/v4/lessons/{lessonJid}/versions")
    @Headers("Authorization: Bearer {token}")
    LessonVersionsResponse getVersions(@Param("token") String token, @Param("lessonJid") String lessonJid);

    @RequestLine("POST /api/v4/lessons/{lessonJid}/versions/{versionHash}/restore")
    @Headers("Authorization: Bearer {token}")
    void restoreVersion(
            @Param("token") String token,
            @Param("lessonJid") String lessonJid,
            @Param("versionHash") String versionHash);

    @RequestLine("POST /api/v4/lessons/{lessonJid}/versions/local/commit")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void commitVersionLocalChanges(
            @Param("token") String token,
            @Param("lessonJid") String lessonJid,
            LessonVersionCommitData data);

    @RequestLine("POST /api/v4/lessons/{lessonJid}/versions/local/rebase")
    @Headers("Authorization: Bearer {token}")
    void rebaseVersionLocalChanges(@Param("token") String token, @Param("lessonJid") String lessonJid);

    @RequestLine("DELETE /api/v4/lessons/{lessonJid}/versions/local")
    @Headers("Authorization: Bearer {token}")
    void discardVersionLocalChanges(@Param("token") String token, @Param("lessonJid") String lessonJid);
}
