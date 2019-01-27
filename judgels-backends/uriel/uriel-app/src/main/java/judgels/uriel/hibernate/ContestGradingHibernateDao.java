package judgels.uriel.hibernate;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.hibernate.AbstractGradingHibernateDao;
import judgels.uriel.persistence.ContestGradingDao;
import judgels.uriel.persistence.ContestGradingModel;

@Singleton
public class ContestGradingHibernateDao extends AbstractGradingHibernateDao<ContestGradingModel>
        implements ContestGradingDao {

    @Inject
    public ContestGradingHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ContestGradingModel createGradingModel() {
        return new ContestGradingModel();
    }

    @Override
    public Class<ContestGradingModel> getGradingModelClass() {
        return ContestGradingModel.class;
    }
}
