package judgels.api.training.chapter.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import java.util.Set;
import judgels.api.catalog.lesson.LessonStatement;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterLessonStatement.class)
public interface ChapterLessonStatement {
    String getDefaultLanguage();
    Set<String> getLanguages();
    ChapterLesson getLesson();
    LessonStatement getStatement();
    Optional<String> getPreviousResourcePath();
    Optional<String> getNextResourcePath();

    class Builder extends ImmutableChapterLessonStatement.Builder {}
}
