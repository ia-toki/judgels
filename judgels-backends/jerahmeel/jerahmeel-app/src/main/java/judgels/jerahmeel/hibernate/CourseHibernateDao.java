package judgels.jerahmeel.hibernate;

import javax.inject.Inject;
import judgels.jerahmeel.persistence.CourseDao;
import judgels.jerahmeel.persistence.CourseModel;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

public class CourseHibernateDao extends JudgelsHibernateDao<CourseModel> implements CourseDao {
    @Inject
    public CourseHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
