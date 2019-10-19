package judgels.jerahmeel.hibernate;

import java.util.List;
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
    public List<CourseChapterModel> selectAllByCourseJid(String courseJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<CourseChapterModel>()
                .putColumnsEq(CourseChapterModel_.courseJid, courseJid)
                .build(), options);
    }
}
