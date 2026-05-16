package judgels.persistence;

import java.util.Optional;

public interface ChapterLessonDao extends Dao<ChapterLessonModel> {
    QueryBuilder<ChapterLessonModel> selectByChapterJid(String contestJid);

    Optional<ChapterLessonModel> selectByLessonJid(String lessonJid);
    Optional<ChapterLessonModel> selectByChapterJidAndLessonAlias(String chapterJid, String lessonAlias);
}
