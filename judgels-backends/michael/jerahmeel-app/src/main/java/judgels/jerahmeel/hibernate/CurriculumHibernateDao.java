package judgels.jerahmeel.hibernate;

import javax.inject.Inject;
import judgels.jerahmeel.persistence.CurriculumDao;
import judgels.jerahmeel.persistence.CurriculumModel;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

public class CurriculumHibernateDao extends JudgelsHibernateDao<CurriculumModel> implements CurriculumDao {
    @Inject
    public CurriculumHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
