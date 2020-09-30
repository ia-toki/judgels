package judgels.jerahmeel.api.course.chapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCourseChapterUserProgressesData.class)
public interface CourseChapterUserProgressesData {
    List<String> getUsernames();

    class Builder extends ImmutableCourseChapterUserProgressesData.Builder {}
}
