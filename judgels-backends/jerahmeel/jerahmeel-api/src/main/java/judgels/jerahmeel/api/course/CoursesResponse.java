package judgels.jerahmeel.api.course;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.jerahmeel.api.curriculum.Curriculum;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCoursesResponse.class)
public interface CoursesResponse {
    List<Course> getData();
    Optional<Curriculum> getCurriculum();
    Map<String, CourseProgress> getCourseProgressesMap();

    class Builder extends ImmutableCoursesResponse.Builder {}
}
