package judgels.chapter;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.chapter.Chapter;
import judgels.api.chapter.ChapterCreateData;
import judgels.api.chapter.ChapterUpdateData;
import judgels.api.chapter.ChaptersResponse;

public interface ChapterClient {
    @RequestLine("GET /api/v2/chapters")
    @Headers("Authorization: Bearer {token}")
    ChaptersResponse getChapters(@Param("token") String token);

    @RequestLine("POST /api/v2/chapters")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Chapter createChapter(@Param("token") String token, ChapterCreateData data);

    @RequestLine("POST /api/v2/chapters/{chapterJid}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Chapter updateChapter(
            @Param("token") String token,
            @Param("chapterJid") String chapterJid,
            ChapterUpdateData data);
}
