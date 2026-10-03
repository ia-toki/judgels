package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.QueryBuilder;
import judgels.persistence.model.ChapterLessonModel;
import judgels.persistence.model.ChapterLessonModel_;

public class ChapterLessonDao extends Dao<ChapterLessonModel> {
    @Inject
    public ChapterLessonDao(DaoData data) {
        super(data);
    }

    public QueryBuilder<ChapterLessonModel> selectByChapterJid(String chapterJid) {
        return select().where(columnEq(ChapterLessonModel_.chapterJid, chapterJid));
    }

    public Optional<ChapterLessonModel> selectByLessonJid(String lessonJid) {
        return select().where(columnEq(ChapterLessonModel_.lessonJid, lessonJid)).unique();
    }

    public Optional<ChapterLessonModel> selectByChapterJidAndLessonAlias(String chapterJid, String lessonAlias) {
        return select()
                .where(columnEq(ChapterLessonModel_.chapterJid, chapterJid))
                .where(columnEq(ChapterLessonModel_.alias, lessonAlias))
                .unique();
    }
}
