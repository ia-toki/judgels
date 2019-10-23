package judgels.jerahmeel.api.chapter.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.sandalphon.api.lesson.LessonStatement;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterLessonStatement.class)
public interface ChapterLessonStatement {
    ChapterLesson getLesson();
    LessonStatement getStatement();

    class Builder extends ImmutableChapterLessonStatement.Builder {}
}
