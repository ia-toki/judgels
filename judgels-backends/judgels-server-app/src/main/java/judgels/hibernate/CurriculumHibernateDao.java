package judgels.hibernate;

import jakarta.inject.Inject;
import judgels.persistence.CurriculumDao;
import judgels.persistence.CurriculumModel;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

public class CurriculumHibernateDao extends JudgelsHibernateDao<CurriculumModel> implements CurriculumDao {
    @Inject
    public CurriculumHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
