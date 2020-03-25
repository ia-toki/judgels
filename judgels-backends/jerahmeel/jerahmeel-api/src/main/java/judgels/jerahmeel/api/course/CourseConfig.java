package judgels.jerahmeel.api.course;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCourseConfig.class)
public interface CourseConfig {
    boolean canAdminister();

    class Builder extends ImmutableCourseConfig.Builder {}
}
