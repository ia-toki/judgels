package judgels.jerahmeel.api.course.chapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCourseChapter.class)
public interface CourseChapter {
    String getAlias();
    String getChapterJid();

    class Builder extends ImmutableCourseChapter.Builder {}
}
