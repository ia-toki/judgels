package judgels.jerahmeel.hibernate;

import java.util.List;
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
    public List<ChapterLessonModel> selectAllByChapterJid(String chapterJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ChapterLessonModel>()
                .putColumnsEq(ChapterLessonModel_.chapterJid, chapterJid)
                .putColumnsEq(ChapterLessonModel_.status, "VISIBLE")
                .build(), options);
    }
}
