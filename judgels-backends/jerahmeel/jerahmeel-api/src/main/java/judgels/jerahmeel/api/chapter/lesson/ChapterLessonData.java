package judgels.jerahmeel.api.chapter.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterLessonData.class)
public interface ChapterLessonData {
    String getAlias();
    String getSlug();

    class Builder extends ImmutableChapterLessonData.Builder {}
}
