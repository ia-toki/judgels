package judgels.jerahmeel.api.course;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCourseCreateData.class)
public interface CourseCreateData {
    String getSlug();
    String getName();
    Optional<String> getDescription();

    class Builder extends ImmutableCourseCreateData.Builder {}
}
