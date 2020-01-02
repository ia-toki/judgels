package judgels.jerahmeel.api.course;

import org.immutables.value.Value;

@Value.Immutable
public interface CourseCreateData {
    String getSlug();

    class Builder extends ImmutableCourseCreateData.Builder {}
}
