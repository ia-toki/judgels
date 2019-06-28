package judgels.uriel.hibernate;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.persistence.ContestGroupDao;
import judgels.uriel.persistence.ContestGroupModel;

@Singleton
public class ContestGroupHibernateDao extends JudgelsHibernateDao<ContestGroupModel> implements ContestGroupDao {
    @Inject
    public ContestGroupHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
