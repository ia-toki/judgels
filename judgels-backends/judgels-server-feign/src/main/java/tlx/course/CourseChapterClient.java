package tlx.course;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import tlx.api.course.chapter.CourseChapterResponse;
import tlx.api.course.chapter.CourseChapterUserProgressesData;
import tlx.api.course.chapter.CourseChapterUserProgressesResponse;
import tlx.api.course.chapter.CourseChaptersResponse;

public interface CourseChapterClient {
    @RequestLine("GET /api/v2/courses/{courseJid}/chapters")
    @Headers("Authorization: Bearer {token}")
    CourseChaptersResponse getChapters(@Param("token") String token, @Param("courseJid") String courseJid);

    @RequestLine("GET /api/v2/courses/{courseJid}/chapters/{chapterAlias}")
    @Headers("Authorization: Bearer {token}")
    CourseChapterResponse getChapter(
            @Param("token") String token,
            @Param("courseJid") String courseJid,
            @Param("chapterAlias") String chapterAlias);

    @RequestLine("POST /api/v2/courses/{courseJid}/chapters/user-progresses")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    CourseChapterUserProgressesResponse getChapterUserProgresses(
            @Param("token") String token,
            @Param("courseJid") String courseJid,
            CourseChapterUserProgressesData data);
}
