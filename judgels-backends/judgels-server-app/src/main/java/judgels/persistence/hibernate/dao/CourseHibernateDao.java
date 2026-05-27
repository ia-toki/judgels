package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.dao.CourseDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.persistence.model.CourseModel;
import judgels.persistence.model.CourseModel_;

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
