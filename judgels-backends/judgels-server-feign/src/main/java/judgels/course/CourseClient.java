package judgels.course;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.api.course.Course;
import judgels.api.course.CourseCreateData;
import judgels.api.course.CourseUpdateData;
import judgels.api.course.CoursesResponse;

public interface CourseClient {
    @RequestLine("GET /api/v2/courses")
    @Headers("Authorization: Bearer {token}")
    CoursesResponse getCourses(@Param("token") String token);

    @RequestLine("POST /api/v2/courses")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Course createCourse(@Param("token") String token, CourseCreateData data);

    @RequestLine("GET /api/v2/courses/slug/{courseSlug}")
    @Headers("Authorization: Bearer {token}")
    Course getCourseBySlug(@Param("token") String token, @Param("courseSlug") String courseSlug);

    @RequestLine("POST /api/v2/courses/{courseJid}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Course updateCourse(
            @Param("token") String token,
            @Param("courseJid") String courseJid,
            CourseUpdateData data);
}
