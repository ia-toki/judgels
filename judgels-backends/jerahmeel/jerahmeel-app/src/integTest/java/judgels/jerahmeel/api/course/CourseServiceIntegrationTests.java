package judgels.jerahmeel.api.course;

import static judgels.jerahmeel.api.course.CourseErrors.SLUG_ALREADY_EXISTS;
import static judgels.jerahmeel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.jerahmeel.api.mocks.MockJophiel.USER_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.palantir.conjure.java.api.errors.RemoteException;
import java.util.Optional;
import judgels.jerahmeel.api.AbstractTrainingServiceIntegrationTests;
import org.junit.jupiter.api.Test;

class CourseServiceIntegrationTests extends AbstractTrainingServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Course courseA = courseService.createCourse(ADMIN_HEADER, new CourseCreateData.Builder()
                .slug("course-a")
                .build());
        courseService.updateCourse(ADMIN_HEADER, courseA.getJid(), new CourseUpdateData.Builder()
                .name("Course A")
                .description("This is course A")
                .build());
        Course courseB = courseService.createCourse(ADMIN_HEADER, new CourseCreateData.Builder()
                .slug("course-b")
                .build());

        assertThatThrownBy(() -> courseService
                .createCourse(ADMIN_HEADER, new CourseCreateData.Builder().slug("course-a").build()))
                .isInstanceOf(RemoteException.class)
                .hasMessageContaining(SLUG_ALREADY_EXISTS.name());

        assertThatThrownBy(() -> courseService
                .updateCourse(ADMIN_HEADER, courseB.getJid(), new CourseUpdateData.Builder().slug("course-a").build()))
                .isInstanceOf(RemoteException.class)
                .hasMessageContaining(SLUG_ALREADY_EXISTS.name());

        CoursesResponse response = courseService.getCourses(Optional.of(ADMIN_HEADER));
        assertThat(response.getData().size()).isEqualTo(2);
        assertThat(response.getConfig().canAdminister()).isTrue();

        // as user

        response = courseService.getCourses(Optional.of(USER_HEADER));
        assertThat(response.getData().size()).isEqualTo(2);
        assertThat(response.getConfig().canAdminister()).isFalse();

        courseA = courseService.getCourseBySlug(Optional.empty(), "course-a");
        assertThat(courseA.getSlug()).isEqualTo("course-a");
        assertThat(courseA.getName()).isEqualTo("Course A");
        assertThat(courseA.getDescription()).isEqualTo("This is course A");

        assertThat(courseService.getCourseBySlug(Optional.empty(), "course-b")).isEqualTo(courseB);
    }
}
