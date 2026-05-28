package tlx.course;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;
import tlx.api.course.chapter.CourseChapter;

public interface CourseChapterAdminClient {
    @RequestLine("PUT /api/v2/admin/courses/{courseJid}/chapters")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void setChapters(
            @Param("token") String token,
            @Param("courseJid") String courseJid,
            List<CourseChapter> data);
}
