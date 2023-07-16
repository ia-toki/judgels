package judgels.jerahmeel.api;

import static judgels.jerahmeel.api.course.CourseErrors.SLUG_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.jerahmeel.api.course.Course;
import judgels.jerahmeel.api.course.CourseCreateData;
import judgels.jerahmeel.api.course.CourseUpdateData;
import org.junit.jupiter.api.Test;

class CourseApiIntegrationTests extends BaseJerahmeelApiIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Course courseA = courseClient.createCourse(adminToken, new CourseCreateData.Builder()
                .slug("course-a")
                .name("Course A")
                .description("This is course A")
                .build());

        assertThat(courseA.getSlug()).isEqualTo("course-a");
        assertThat(courseA.getName()).isEqualTo("Course A");
        assertThat(courseA.getDescription()).isEqualTo("This is course A");

        Course courseB = courseClient.createCourse(adminToken, new CourseCreateData.Builder()
                .slug("course-b")
                .name("Course B")
                .build());

        assertThat(courseB.getSlug()).isEqualTo("course-b");

        assertBadRequest(() -> courseClient
                .createCourse(adminToken, new CourseCreateData.Builder().slug("course-a").name("Course A").build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);

        assertBadRequest(() -> courseClient
                .updateCourse(adminToken, courseB.getJid(), new CourseUpdateData.Builder().slug("course-a").build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);

        var response = courseClient.getCourses(adminToken);
        assertThat(response.getData()).containsExactly(courseA, courseB);

        // as user

        assertForbidden(() -> courseClient
                .createCourse(userToken, new CourseCreateData.Builder().slug("course-c").name("Course C").build()));

        response = courseClient.getCourses(userToken);
        assertThat(response.getData()).containsExactly(courseA, courseB);

        assertThat(courseClient.getCourseBySlug("", "course-a")).isEqualTo(courseA);
    }
}
