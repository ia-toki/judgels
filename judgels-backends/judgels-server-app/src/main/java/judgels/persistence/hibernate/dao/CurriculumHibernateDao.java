package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import judgels.persistence.dao.CurriculumDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.persistence.model.CurriculumModel;

public class CurriculumHibernateDao extends JudgelsHibernateDao<CurriculumModel> implements CurriculumDao {
    @Inject
    public CurriculumHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
