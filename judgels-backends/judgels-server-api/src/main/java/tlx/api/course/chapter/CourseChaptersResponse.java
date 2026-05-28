package tlx.api.course.chapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import org.immutables.value.Value;
import tlx.api.chapter.ChapterInfo;
import tlx.api.chapter.ChapterProgress;

@Value.Immutable
@JsonDeserialize(as = ImmutableCourseChaptersResponse.class)
public interface CourseChaptersResponse {
    List<CourseChapter> getData();
    Map<String, ChapterInfo> getChaptersMap();
    Map<String, ChapterProgress> getChapterProgressesMap();

    class Builder extends ImmutableCourseChaptersResponse.Builder {}
}
