package judgels.jerahmeel.hibernate;

import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.ChapterLessonDao;
import judgels.jerahmeel.persistence.ChapterLessonModel;
import judgels.jerahmeel.persistence.ChapterLessonModel_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class ChapterLessonHibernateDao extends HibernateDao<ChapterLessonModel> implements ChapterLessonDao {
    @Inject
    public ChapterLessonHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public QueryBuilder<ChapterLessonModel> selectByChapterJid(String chapterJid) {
        return select().where(columnEq(ChapterLessonModel_.chapterJid, chapterJid));
    }

    @Override
    public Optional<ChapterLessonModel> selectByLessonJid(String lessonJid) {
        return select().where(columnEq(ChapterLessonModel_.lessonJid, lessonJid)).unique();
    }

    @Override
    public Optional<ChapterLessonModel> selectByChapterJidAndLessonAlias(String chapterJid, String lessonAlias) {
        return select()
                .where(columnEq(ChapterLessonModel_.chapterJid, chapterJid))
                .where(columnEq(ChapterLessonModel_.alias, lessonAlias))
                .unique();
    }
}
