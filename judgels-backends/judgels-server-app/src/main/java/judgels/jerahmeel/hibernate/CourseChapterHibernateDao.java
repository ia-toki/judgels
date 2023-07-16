package judgels.jerahmeel.hibernate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.CourseChapterDao;
import judgels.jerahmeel.persistence.CourseChapterModel;
import judgels.jerahmeel.persistence.CourseChapterModel_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class CourseChapterHibernateDao extends HibernateDao<CourseChapterModel> implements CourseChapterDao {
    @Inject
    public CourseChapterHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<CourseChapterModel> selectByCourseJidAndChapterAlias(String courseJid, String chapterAlias) {
        return select()
                .where(columnEq(CourseChapterModel_.courseJid, courseJid))
                .where(columnEq(CourseChapterModel_.alias, chapterAlias))
                .unique();
    }

    @Override
    public Optional<CourseChapterModel> selectByChapterJid(String chapterJid) {
        return select().where(columnEq(CourseChapterModel_.chapterJid, chapterJid)).unique();
    }

    @Override
    public QueryBuilder<CourseChapterModel> selectByCourseJid(String courseJid) {
        return select().where(columnEq(CourseChapterModel_.courseJid, courseJid));
    }

    @Override
    public List<CourseChapterModel> selectAllByChapterJids(Collection<String> chapterJids) {
        return select().where(columnIn(CourseChapterModel_.chapterJid, chapterJids)).all();
    }
}
