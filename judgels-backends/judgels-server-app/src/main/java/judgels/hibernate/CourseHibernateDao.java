package judgels.hibernate;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.CourseDao;
import judgels.persistence.CourseModel;
import judgels.persistence.CourseModel_;
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
