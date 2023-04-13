package judgels.jerahmeel.api.course.chapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCourseChapterResponse.class)
public interface CourseChapterResponse {
    String getJid();
    String getName();
    List<String> getLessonAliases();

    class Builder extends ImmutableCourseChapterResponse.Builder {}
}
