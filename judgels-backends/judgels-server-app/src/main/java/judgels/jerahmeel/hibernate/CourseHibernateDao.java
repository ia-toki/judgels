package judgels.jerahmeel.hibernate;

import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.CourseDao;
import judgels.jerahmeel.persistence.CourseModel;
import judgels.jerahmeel.persistence.CourseModel_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

public class CourseHibernateDao extends JudgelsHibernateDao<CourseModel> implements CourseDao {
    @Inject
    public CourseHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<CourseModel> selectBySlug(String courseSlug) {
        return select().where(columnEq(CourseModel_.slug, courseSlug)).unique();
    }
}
