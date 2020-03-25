package judgels.jerahmeel.api.course;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCourseCreateData.class)
public interface CourseCreateData {
    String getSlug();

    class Builder extends ImmutableCourseCreateData.Builder {}
}
