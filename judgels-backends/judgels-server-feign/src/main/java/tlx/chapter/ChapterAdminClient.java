package tlx.chapter;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import tlx.api.chapter.Chapter;
import tlx.api.chapter.ChapterCreateData;
import tlx.api.chapter.ChapterUpdateData;
import tlx.api.chapter.ChaptersResponse;

public interface ChapterAdminClient {
    @RequestLine("GET /api/v2/admin/chapters")
    @Headers("Authorization: Bearer {token}")
    ChaptersResponse getChapters(@Param("token") String token);

    @RequestLine("POST /api/v2/admin/chapters")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Chapter createChapter(@Param("token") String token, ChapterCreateData data);

    @RequestLine("POST /api/v2/admin/chapters/{chapterJid}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Chapter updateChapter(
            @Param("token") String token,
            @Param("chapterJid") String chapterJid,
            ChapterUpdateData data);
}
