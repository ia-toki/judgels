package judgels.jerahmeel.api.course;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCourseProgress.class)
public interface CourseProgress {
    int getSolvedChapters();
    int getTotalChapters();
    int getTotalSolvableChapters();
    int getSolvedProblems();
    int getTotalProblems();

    class Builder extends ImmutableCourseProgress.Builder {}
}
