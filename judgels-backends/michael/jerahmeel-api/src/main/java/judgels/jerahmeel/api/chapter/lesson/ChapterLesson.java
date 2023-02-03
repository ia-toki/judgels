package judgels.jerahmeel.api.chapter.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterLesson.class)
public interface ChapterLesson {
    String getAlias();
    String getLessonJid();

    class Builder extends ImmutableChapterLesson.Builder {}
}
