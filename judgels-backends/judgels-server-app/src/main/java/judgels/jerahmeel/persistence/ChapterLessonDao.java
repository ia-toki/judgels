package judgels.jerahmeel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;

public interface ChapterLessonDao extends Dao<ChapterLessonModel> {
    QueryBuilder<ChapterLessonModel> selectByChapterJid(String contestJid);

    Optional<ChapterLessonModel> selectByLessonJid(String lessonJid);
    Optional<ChapterLessonModel> selectByChapterJidAndLessonAlias(String chapterJid, String lessonAlias);
}
