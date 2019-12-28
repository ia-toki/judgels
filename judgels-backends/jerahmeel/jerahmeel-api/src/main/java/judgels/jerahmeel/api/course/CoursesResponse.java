package judgels.jerahmeel.api.course;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCoursesResponse.class)
public interface CoursesResponse {
    List<Course> getData();
    Optional<String> getCurriculumDescription();

    class Builder extends ImmutableCoursesResponse.Builder {}
}
