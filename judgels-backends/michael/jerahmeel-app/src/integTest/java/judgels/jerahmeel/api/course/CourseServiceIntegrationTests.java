package judgels.jerahmeel.api.course;

import static judgels.jerahmeel.api.course.CourseErrors.SLUG_ALREADY_EXISTS;
import static judgels.jerahmeel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.jerahmeel.api.mocks.MockJophiel.USER_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import judgels.jerahmeel.api.AbstractTrainingServiceIntegrationTests;
import org.junit.jupiter.api.Test;

class CourseServiceIntegrationTests extends AbstractTrainingServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Course courseA = courseService.createCourse(ADMIN_HEADER, new CourseCreateData.Builder()
                .slug("course-a")
                .name("Course A")
                .description("This is course A")
                .build());

        assertThat(courseA.getSlug()).isEqualTo("course-a");
        assertThat(courseA.getName()).isEqualTo("Course A");
        assertThat(courseA.getDescription()).isEqualTo("This is course A");

        Course courseB = courseService.createCourse(ADMIN_HEADER, new CourseCreateData.Builder()
                .slug("course-b")
                .name("Course B")
                .build());

        assertThat(courseB.getSlug()).isEqualTo("course-b");

        assertThatThrownBy(() -> courseService
                .createCourse(ADMIN_HEADER, new CourseCreateData.Builder().slug("course-a").name("Course A").build()))
                .hasFieldOrPropertyWithValue("code", 400)
                .hasMessageContaining(SLUG_ALREADY_EXISTS);

        assertThatThrownBy(() -> courseService
                .updateCourse(ADMIN_HEADER, courseB.getJid(), new CourseUpdateData.Builder().slug("course-a").build()))
                .hasFieldOrPropertyWithValue("code", 400)
                .hasMessageContaining(SLUG_ALREADY_EXISTS);

        CoursesResponse response = courseService.getCourses(Optional.of(ADMIN_HEADER));
        assertThat(response.getData()).containsExactly(courseA, courseB);

        // as user

        assertThatThrownBy(() -> courseService
                .createCourse(USER_HEADER, new CourseCreateData.Builder().slug("course-c").name("Course C").build()))
                .hasFieldOrPropertyWithValue("code", 403);

        response = courseService.getCourses(Optional.of(USER_HEADER));
        assertThat(response.getData()).containsExactly(courseA, courseB);

        assertThat(courseService.getCourseBySlug(Optional.empty(), "course-a")).isEqualTo(courseA);
    }
}
