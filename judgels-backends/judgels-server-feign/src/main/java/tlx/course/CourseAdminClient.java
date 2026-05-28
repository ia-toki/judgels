package tlx.course;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import tlx.api.course.Course;
import tlx.api.course.CourseCreateData;
import tlx.api.course.CourseUpdateData;

public interface CourseAdminClient {
    @RequestLine("POST /api/v2/admin/courses")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Course createCourse(@Param("token") String token, CourseCreateData data);

    @RequestLine("POST /api/v2/admin/courses/{courseJid}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Course updateCourse(
            @Param("token") String token,
            @Param("courseJid") String courseJid,
            CourseUpdateData data);
}
