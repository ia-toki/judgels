package judgels.uriel.hibernate;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import judgels.uriel.persistence.ContestLogDao;
import judgels.uriel.persistence.ContestLogModel;

@Singleton
public class ContestLogHibernateDao extends UnmodifiableHibernateDao<ContestLogModel> implements ContestLogDao {
    @Inject
    public ContestLogHibernateDao(HibernateDaoData data) {
        super(data);
    }
}
