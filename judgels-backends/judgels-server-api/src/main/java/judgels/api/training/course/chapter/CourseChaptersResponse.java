package judgels.api.training.course.chapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.api.training.chapter.ChapterInfo;
import judgels.api.training.chapter.ChapterProgress;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCourseChaptersResponse.class)
public interface CourseChaptersResponse {
    List<CourseChapter> getData();
    Map<String, ChapterInfo> getChaptersMap();
    Map<String, ChapterProgress> getChapterProgressesMap();

    class Builder extends ImmutableCourseChaptersResponse.Builder {}
}
