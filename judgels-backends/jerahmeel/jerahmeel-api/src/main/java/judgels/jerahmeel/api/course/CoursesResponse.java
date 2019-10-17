package judgels.jerahmeel.api.course;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCoursesResponse.class)
public interface CoursesResponse {
    List<Course> getData();

    class Builder extends ImmutableCoursesResponse.Builder {}
}
