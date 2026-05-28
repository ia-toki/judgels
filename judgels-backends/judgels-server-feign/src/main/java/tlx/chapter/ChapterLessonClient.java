package tlx.chapter;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;
import tlx.api.chapter.lesson.ChapterLessonData;
import tlx.api.chapter.lesson.ChapterLessonStatement;
import tlx.api.chapter.lesson.ChapterLessonsResponse;

public interface ChapterLessonClient {
    @RequestLine("GET /api/v2/chapters/{chapterJid}/lessons")
    @Headers("Authorization: Bearer {token}")
    ChapterLessonsResponse getLessons(@Param("token") String token, @Param("chapterJid") String chapterJid);

    @RequestLine("GET /api/v2/chapters/{chapterJid}/lessons/{lessonAlias}/statement")
    @Headers("Authorization: Bearer {token}")
    ChapterLessonStatement getLessonStatement(
            @Param("token") String token,
            @Param("chapterJid") String chapterJid,
            @Param("lessonAlias") String lessonAlias);

    @RequestLine("PUT /api/v2/chapters/{chapterJid}/lessons")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setLessons(
            @Param("token") String token,
            @Param("chapterJid") String chapterJid,
            List<ChapterLessonData> data);
}
