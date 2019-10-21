package judgels.jerahmeel.api.chapter.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.sandalphon.api.lesson.LessonInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterLessonsResponse.class)
public interface ChapterLessonsResponse {
    List<ChapterLesson> getData();
    Map<String, LessonInfo> getLessonsMap();

    class Builder extends ImmutableChapterLessonsResponse.Builder {}
}
