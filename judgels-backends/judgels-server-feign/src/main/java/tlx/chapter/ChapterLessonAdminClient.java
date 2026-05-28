package tlx.chapter;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;
import tlx.api.chapter.lesson.ChapterLessonData;

public interface ChapterLessonAdminClient {
    @RequestLine("PUT /api/v2/admin/chapters/{chapterJid}/lessons")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setLessons(
            @Param("token") String token,
            @Param("chapterJid") String chapterJid,
            List<ChapterLessonData> data);
}
