package judgels.uriel.hibernate;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.hibernate.AbstractProgrammingGradingHibernateDao;
import judgels.uriel.persistence.ContestProgrammingGradingDao;
import judgels.uriel.persistence.ContestProgrammingGradingModel;

@Singleton
public class ContestProgrammingGradingHibernateDao extends AbstractProgrammingGradingHibernateDao<
        ContestProgrammingGradingModel> implements ContestProgrammingGradingDao {

    @Inject
    public ContestProgrammingGradingHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ContestProgrammingGradingModel createGradingModel() {
        return new ContestProgrammingGradingModel();
    }

    @Override
    public Class<ContestProgrammingGradingModel> getGradingModelClass() {
        return ContestProgrammingGradingModel.class;
    }
}
