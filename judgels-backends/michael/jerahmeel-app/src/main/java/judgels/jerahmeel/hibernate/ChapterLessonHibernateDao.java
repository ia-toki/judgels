package judgels.jerahmeel.hibernate;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.ChapterLessonDao;
import judgels.jerahmeel.persistence.ChapterLessonModel;
import judgels.jerahmeel.persistence.ChapterLessonModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class ChapterLessonHibernateDao extends HibernateDao<ChapterLessonModel> implements ChapterLessonDao {
    @Inject
    public ChapterLessonHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ChapterLessonModel> selectByLessonJid(String lessonJid) {
        return selectByFilter(new FilterOptions.Builder<ChapterLessonModel>()
                .putColumnsEq(ChapterLessonModel_.lessonJid, lessonJid)
                .build());
    }

    @Override
    public Optional<ChapterLessonModel> selectByChapterJidAndLessonAlias(String chapterJid, String lessonAlias) {
        return selectByFilter(new FilterOptions.Builder<ChapterLessonModel>()
                .putColumnsEq(ChapterLessonModel_.chapterJid, chapterJid)
                .putColumnsEq(ChapterLessonModel_.alias, lessonAlias)
                .build());
    }

    @Override
    public List<ChapterLessonModel> selectAllByChapterJid(String chapterJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ChapterLessonModel>()
                .putColumnsEq(ChapterLessonModel_.chapterJid, chapterJid)
                .build(), options);
    }
}
