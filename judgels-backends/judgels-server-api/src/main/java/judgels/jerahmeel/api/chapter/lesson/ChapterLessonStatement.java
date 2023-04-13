package judgels.jerahmeel.api.chapter.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import judgels.sandalphon.api.lesson.LessonStatement;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterLessonStatement.class)
public interface ChapterLessonStatement {
    String getDefaultLanguage();
    Set<String> getLanguages();
    ChapterLesson getLesson();
    LessonStatement getStatement();

    class Builder extends ImmutableChapterLessonStatement.Builder {}
}
