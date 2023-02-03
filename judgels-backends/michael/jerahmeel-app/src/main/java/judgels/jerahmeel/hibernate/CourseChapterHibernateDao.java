package judgels.jerahmeel.hibernate;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.CourseChapterDao;
import judgels.jerahmeel.persistence.CourseChapterModel;
import judgels.jerahmeel.persistence.CourseChapterModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class CourseChapterHibernateDao extends HibernateDao<CourseChapterModel> implements CourseChapterDao {
    @Inject
    public CourseChapterHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<CourseChapterModel> selectByCourseJidAndChapterAlias(String courseJid, String chapterAlias) {
        return selectByFilter(new FilterOptions.Builder<CourseChapterModel>()
                .putColumnsEq(CourseChapterModel_.courseJid, courseJid)
                .putColumnsEq(CourseChapterModel_.alias, chapterAlias)
                .build());
    }

    @Override
    public Optional<CourseChapterModel> selectByChapterJid(String chapterJid) {
        return selectByUniqueColumn(CourseChapterModel_.chapterJid, chapterJid);
    }

    @Override
    public List<CourseChapterModel> selectAllByCourseJid(String courseJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<CourseChapterModel>()
                .putColumnsEq(CourseChapterModel_.courseJid, courseJid)
                .build(), options);
    }

    @Override
    public List<CourseChapterModel> selectAllByCourseJids(Set<String> courseJids) {
        return selectAll(new FilterOptions.Builder<CourseChapterModel>()
                .putColumnsIn(CourseChapterModel_.courseJid, courseJids)
                .build());
    }

    @Override
    public List<CourseChapterModel> selectAllByChapterJids(Set<String> chapterJids) {
        return selectAll(new FilterOptions.Builder<CourseChapterModel>()
                .putColumnsIn(CourseChapterModel_.chapterJid, chapterJids)
                .build());
    }
}
