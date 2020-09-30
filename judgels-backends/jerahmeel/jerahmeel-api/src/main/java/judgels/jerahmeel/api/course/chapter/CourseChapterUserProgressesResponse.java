package judgels.jerahmeel.api.course.chapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCourseChapterUserProgressesResponse.class)
public interface CourseChapterUserProgressesResponse {
    List<Integer> getTotalProblemsList();
    Map<String, List<Integer>> getUserProgressesMap();

    class Builder extends ImmutableCourseChapterUserProgressesResponse.Builder {}
}
