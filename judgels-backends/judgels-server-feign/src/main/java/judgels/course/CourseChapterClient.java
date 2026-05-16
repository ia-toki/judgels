package judgels.course;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;
import judgels.api.course.chapter.CourseChapter;
import judgels.api.course.chapter.CourseChapterResponse;
import judgels.api.course.chapter.CourseChapterUserProgressesData;
import judgels.api.course.chapter.CourseChapterUserProgressesResponse;
import judgels.api.course.chapter.CourseChaptersResponse;

public interface CourseChapterClient {
    @RequestLine("GET /api/v2/courses/{courseJid}/chapters")
    @Headers("Authorization: Bearer {token}")
    CourseChaptersResponse getChapters(@Param("token") String token, @Param("courseJid") String courseJid);

    @RequestLine("PUT /api/v2/courses/{courseJid}/chapters")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setChapters(
            @Param("token") String token,
            @Param("courseJid") String courseJid,
            List<CourseChapter> data);

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
