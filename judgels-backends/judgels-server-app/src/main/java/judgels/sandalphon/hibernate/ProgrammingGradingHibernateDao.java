package judgels.sandalphon.hibernate;

import javax.inject.Inject;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.persistence.ProgrammingGradingDao;
import judgels.sandalphon.persistence.ProgrammingGradingModel;

public class ProgrammingGradingHibernateDao
        extends AbstractProgrammingGradingHibernateDao<ProgrammingGradingModel>
        implements ProgrammingGradingDao {

    @Inject
    public ProgrammingGradingHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ProgrammingGradingModel createGradingModel() {
        return new ProgrammingGradingModel();
    }

    @Override
    public Class<ProgrammingGradingModel> getGradingModelClass() {
        return ProgrammingGradingModel.class;
    }
}
