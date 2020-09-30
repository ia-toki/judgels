package judgels.jerahmeel.api.course.chapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCourseChapterUserProgressesData.class)
public interface CourseChapterUserProgressesData {
    Set<String> getUsernames();

    class Builder extends ImmutableCourseChapterUserProgressesData.Builder {}
}
